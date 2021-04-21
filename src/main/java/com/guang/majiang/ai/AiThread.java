package com.guang.majiang.ai;

import com.guang.majiang.common.Direction;
import com.guang.majiang.common.SpecialEvent;
import com.guang.majiang.event.*;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerNode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生成者
 *
 * @ClassName AiThread
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/3 19:24
 * @Version 1.0
 **/
public class AiThread extends Thread {

    // 玩家状态
    private PlayerNode<Player> playerNode;

    // 玩家线程对应的锁
    private ReentrantLock lock;

    // 玩家对应的条件变量
    private Condition waitCondition;

    // 下家对应的条件变量
    private Condition nextDirCondition;

    private Pane pane;

    private String name;

    // 一个任务队列
    // 有界阻塞队列
    private LinkedBlockingQueue<Runnable> taskQueue;

    private LinkedList<CardImage> cards;

    private LinkedBlockingQueue<Runnable> playerEventTaskQueue;

    private List<ImageView> eventImages;

    private AtomicReference<SpecialEvent> state;

    public AiThread(PlayerNode<Player> playerNode, LinkedBlockingQueue<Runnable> taskQueue,
                    ReentrantLock lock, Condition waitCondition,
                    Condition nextDirCondition, Pane pane, LinkedList<CardImage> cards,
                      LinkedBlockingQueue<Runnable> playerEventTaskQueue, List<ImageView> eventImages,
                    AtomicReference<SpecialEvent> state) {
        this.playerNode = playerNode;
        this.taskQueue = taskQueue;
        this.lock = lock;
        this.waitCondition = waitCondition;
        this.name = playerNode.item.getName();
        this.pane = pane;
        this.cards = cards;
        this.nextDirCondition = nextDirCondition;
        this.playerEventTaskQueue = playerEventTaskQueue;
        this.eventImages = eventImages;
        this.state = state;
    }

    /**
     * 1. 假设 甲 是玩家 并且是庄家，首先是 摸手牌
     *      当甲 点击出牌后，如何唤醒其他线程 首先进行判断是否有特殊任务触发
     *      当 没有之后，下家进行摸牌任务，并进行出牌，同 甲一样
     *
     * 2. 假设 甲 不是庄家，同理，庄家线程首先进行出牌，唤醒其他线程是否需要
     *    触发特殊任务
     *
     * 3. 玩家游戏阶段：（1） 初始化  （2）进行中   （3） 结束
     */
    @Override
    public void run() {
        // 产生特定的任务提交到任务队列中
        // 每个玩家都有自己的回合
        // 当不是自己的回合时，不能提交任务，除非触发特殊情况（碰、杠、胡）
        // 所以任意一个玩家出牌，都需要唤醒其他线程，进行判断
        // 当不是自己的回合时，进入锁定状态（无法提交任务）
        // 如何做到事件触发机制

        // 1.第一个任务 判断 玩家是否摸牌
        // 思路：庄家线程首先 加载 4 张牌，并陷入阻塞，唤醒下家线程，加载 4张牌
        // 一直到所有玩家手牌加载完成
        // 最简单做法 设置 4 个单独的条件变量 对应 4 个线程
        PlayerNode<Player> cur = playerNode;
        Player player = cur.item;
        int maxCard = player.isBookmaker() ? 14 : 13;
        while (true) {
            // 先尝试获取锁
            lock.lock();
            try {
                while (player.getIsRound() != 1) {
                    // 当前不是自己的回合，进入等待状态
                    waitCondition.await();
                    System.out.println(name + " " + player.getDirection() + "被唤醒！");
                }
                List<CardImage> handCards = player.getPlayerCard().getCards();

                // 判断玩家状态
                if(player.getGameState() == 0) {
                    // 发牌
                    // 首先获取玩家当前手牌
                    if(handCards.size() < maxCard) {
                        System.out.println("现在是 " + player.getDirection() + " 的回合！获取手牌！");
                        taskQueue.offer(new InitHandCardTask(playerNode, pane, cards, taskQueue, playerEventTaskQueue));
                    }
                    if(handCards.size() == maxCard) {
                        // 整理手牌
                        taskQueue.offer(new SortedHandCardTask(playerNode));
                        player.setGameState(1);
                    }
                    // 需要确定所有玩家手牌数都已经拿完
                    Thread.sleep(100);
                    // 唤醒下家
                    // 更改下家回合状态
                    rebirth(cur, player);
                } else if(player.getGameState() == 1) {
                    // 庄家如果是第一次出牌，则不用摸牌
                    // 否则则直接出一张牌
                    // 直接判断手牌标准

                    // 判断是否触发了玩家的特殊事件业务流程
                    SpecialEvent t = state.get();
                    SpecialEvent event;
                    if(t != null && t != SpecialEvent.IGNORE) {
                        // 说明有 ai 触发玩家特殊事件
                        // 判断当前线程是否为 玩家线程
                        if(player.getDirection() == Direction.UNDER) {
                            // 1. 碰事件 回合跳到玩家身上 且不触发摸牌事件
                            // 2. 杠事件 回合跳到回家身上，且先摸牌
                            // 3. 胡事件 游戏结束
                            // 4. 过事件 照常继续
                            if(t == SpecialEvent.PONG) {
                                taskQueue.offer(playerEventTaskQueue.take());
                                state.compareAndSet(t, null);
                            }else if(t == SpecialEvent.KONG) {
                                addHandCard(player);
                                taskQueue.offer(playerEventTaskQueue.take());
                                state.compareAndSet(t, null);
                            }else if(t == SpecialEvent.HU) {
                                System.out.println("游戏结束！");
                                System.exit(1);
                            }
                        }
                    }else if(handCards.size() == 14 && player.isBookmaker()) {
                        // 直接出牌
                       event = handCard(player);
                       if(event != null && event != SpecialEvent.IGNORE) {
                           state.compareAndSet(t, event);
                       }
                    }else if(handCards.size() > 0){
                        addHandCard(player);
                        event = handCard(player);
                        if(event != null && event != SpecialEvent.IGNORE) {
                            state.compareAndSet(t, event);
                        }
                    }
                    Thread.sleep(1000);
                    rebirth(cur, player);
                }
            } catch (InterruptedException e) {
                System.out.println(name + " 线程被打断！");
            } finally {
                lock.unlock();
            }
        }
    }

    private void rebirth(PlayerNode<Player> cur, Player player) throws InterruptedException {
        player.setIsRound(0);
        cur.next.item.setIsRound(1);
        nextDirCondition.signal();
        waitCondition.await();
    }

    /**
     * @param player 玩家
     * @throws InterruptedException
     */
    private SpecialEvent handCard(Player player) throws InterruptedException {
        if(player.getDirection() != Direction.UNDER) {
            // 出牌之前 唤醒其他玩家 需要判断是否 可以触发特殊事件
            TakeOutHandCardTask e = new TakeOutHandCardTask(playerNode, pane, true, null);
            taskQueue.offer(e);
            CardImage takeOut;
            while (e.getTakeOutCard() == null) {

            }
            takeOut = e.getTakeOutCard();
            System.out.println(player.getDirection() + " takeOut: " + takeOut.getValue());
            // 出牌成功
            // 简单起见 只有玩家可以 触发特殊事件
            PlayerNode<Player> p = playerNode;
            while (p.item.getDirection() != Direction.UNDER) {
                p = p.next;
            }
            // 判断是否触发特殊事件
            List<CardImage> handCards = p.item.getPlayerCard().getCards();
            List<CardImage[]> bump = p.item.getPlayerCard().getBump();
            int res = isSpecialEvent(takeOut, handCards, () -> false);
            if(res != 0) {
                // 触发玩家的特殊事件
                System.out.println("玩家有特殊事件！" + res);
                SpecialEventTask specialTask = new SpecialEventTask(res, eventImages, takeOut, handCards, bump, player);
                taskQueue.offer(specialTask);
                while (!specialTask.isOver()) {
                }
                System.out.println("玩家事件结束！");
                return specialTask.getEvent();
            }
        }else {
            taskQueue.offer(playerEventTaskQueue.take());
            return null;
        }
        return null;
    }

    private void addHandCard(Player player) {
        if(cards.isEmpty()) {
            System.out.println("没牌了！");
            return;
        }
        CardImage handCard = cards.poll();
        if(player.getDirection() != Direction.UNDER) {
            System.out.println("先摸牌！" + name + player.getDirection() + handCard.getValue());
            taskQueue.offer(new TakeHandCardTask(player, pane, handCard));
        }else {
            System.out.println("先摸牌！" + name + player.getDirection() + handCard.getValue());
            // 插入合适的位置
            handCard.getImageView().
                    setOnMouseClicked(new CardHandler(playerNode, handCard, playerEventTaskQueue));
            taskQueue.offer(new PlayerTakeTask(player, pane, handCard));
        }
    }

    private int isSpecialEvent(CardImage takeOut, List<CardImage> handCard, PlayerGameOver game) {
        // 碰：0/1 杠 0/1 胡 0/1 默认为 0/1
        // 碰杠 00 01 10 11
        // 111
        int res = 0b111;
        // 首先判断是否满足胡
        // 胡牌算法
        if(!isPong(takeOut, handCard)) {
            res &= 0b110;
        }
        // 判断是否有杠
        if(!isKong(takeOut, handCard)) {
            res &= 0b101;
        }
        if(!game.isOver()) {
            res &= 0b011;
        }
        return res;
    }


    private boolean isKong(CardImage takeOut, List<CardImage> handCard) {
        // 找出 有序数组中的元素个数
        // 可以使用二分法
        // 找到左边界 i, 右边界 j
        // nums[i - 1] < t nums[j + 1] > t
        int count = 0;
        for (CardImage card : handCard) {
            if(card.compareTo(takeOut) == 0) {
                count ++;
            }
        }
        return count == 3;
    }

    private boolean isPong(CardImage takeOut, List<CardImage> handCard) {
        int count = 0;
        for (CardImage card : handCard) {
            if(card.compareTo(takeOut) == 0) {
                count ++;
            }
        }
        return count == 2;
    }
}
