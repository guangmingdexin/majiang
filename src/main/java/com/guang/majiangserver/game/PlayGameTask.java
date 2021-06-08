package com.guang.majiangserver.game;

import com.guang.majiangclient.client.algorithm.Algorithm;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.*;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.util.ClassUtil;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangserver.util.ExtendedExecutor;
import com.guang.majiangserver.util.ResponseUtil;
import com.guang.majiangserver.util.ServerCache;
import io.netty.channel.Channel;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName PlayGameTask
 * @Author guangmingdexin
 * @Date 2021/5/21 16:11
 * @Version 1.0
 **/
public class PlayGameTask {

    private static final ExecutorService TASK_POOL = new ExtendedExecutor(
            16,
            16,
            300,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactory() {

                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "game_business_thread_" + threadNumber.getAndIncrement());
                }
            });


    private static void takeOutCards(PlayGameHandCardsInfo info, int needPlayNum) {
        Room room = info.getRoom();
        Set<GameUser> players = room.getPlayers();
        // 确定谁是庄家
        int r = new Random().nextInt(needPlayNum);
        Direction marker = Direction.valueOf(r);
        info.setMarker(marker);
        // 首先设置默认玩家回合
        List<Integer> cards = info.getCards();

        while (true) {
            int j = 1;
            for (GameUser player : players) {
                // 庄家 14 张牌，普通玩家 13 张牌
                GameInfoCard gameInfoCard = player.getGameInfoCard();
                int maxHandCard =  player.getDirection() == marker ? 14 : 13;
                if(gameInfoCard == null) {
                    gameInfoCard = new GameInfoCard(new ArrayList<>(), maxHandCard, marker, player.getDirection());
                    player.setGameInfoCard(gameInfoCard);
                }
                if(gameInfoCard.getUseCards().size() < maxHandCard) {
                    // 继续发牌
                    for (int i = 0; i < 4 && gameInfoCard.getUseCards().size() < 14; i++) {
                        gameInfoCard.getUseCards().add(cards.remove(0));
                    }
                }else {
                    j++;
                }
            }
            if(j > needPlayNum) {
                break;
            }
        }
    }

    /**
     * 初始化发牌
     *
     * @param info 手牌信息
     * @param needPlayNum 玩家人数
     */
    public static void addTakeOutCardsTask(PlayGameHandCardsInfo info, int needPlayNum) {
        TASK_POOL.execute(() -> {
            takeOutCards(info, needPlayNum);
            // 生成一个任务
            Room room = info.getRoom();
            for (GameUser player : room.getPlayers()) {
                GameInfoCard gameInfoCard = player.getGameInfoCard();
                List<Integer> useCards = gameInfoCard.getUseCards();
                Collections.sort(useCards);
                // TODO 是否能保证发送 room 对象不同（对象复制）
                room.setNumCards(useCards);

                if(gameInfoCard.around()) {
                    int res = 0;
                    int mask = 7;
                    for (Integer useCard : useCards) {
                        int count = Algorithm.sortCountArr(useCards, useCard);
                        if(count == 4) {
                            res = mask & GameEvent.Gang.intValue();
                            room.setRes(res);
                        }
                    }

                    boolean hu = Algorithm.isHu(useCards);
                    if(hu) {
                        res = mask & GameEvent.Hu.intValue();
                    }
                    room.setRes(res);
                }

                Channel channel = ServerCache.getChannel(player.getUserId());
                AuthResponseMessage response = new AuthResponseMessage();
                ResponseUtil.responseBuildFactory(response, ClassUtil.copyObj(room), 200, Event.RANDOMGAME, "发牌成功！", true);
                channel.writeAndFlush(response);
                System.out.println("发送成功！");
                room.setRes(0);
                room.setInfos(null);
            }
        });

    }

    /**
     * 出牌
     *
     * @param userId 用户 Id
     * @param info 信息包
     * @param room 房间
     */
    public static void addTakeOutCardTask(long userId, PlayGameInfo info, Room room) {
        TASK_POOL.execute(() -> {
            HashSet<GameUser> players = room.getPlayers();
            PlayGameInfo load = info;
            for (GameUser player : players) {
                GameInfoCard infoCards = player.getGameInfoCard();
                List<Integer> useCards = infoCards.getUseCards();
                // 计算其他玩家是否产生特殊事件
                if(player.getUserId() != userId) {
                    int mask = 7;
                    int res = 0;
                    int count = Algorithm.sortCountArr(useCards, info.getValue());
                    if(count == 2) {
                        res = mask & GameEvent.Pong.intValue();
                    }else if(count == 3) {
                        res = mask & GameEvent.Pong.intValue() & GameEvent.Gang.intValue();
                    }
                    List<Integer> temp = new ArrayList<>(useCards);
                    Algorithm.sortInsert(temp, info.getValue());
                    if(Algorithm.isHu(temp)) {
                        res = mask & GameEvent.Hu.intValue();
                    }
                    load = new PlayGameInfo(info, res);
                }

                Channel channel = ServerCache.getChannel(player.getUserId());
                AuthResponseMessage response = new AuthResponseMessage();
                ResponseUtil.responseBuildFactory(response, load, 200, Event.GAMEINFO, "出牌成功！", true);
                channel.writeAndFlush(response);
            }
        });
    }

    /**
     * 摸牌
     *
     * @param room 房间
     *
     */
    public static void addSpcialTask(Room room) {

        TASK_POOL.execute(() -> {
            List<PlayGameInfo> infos = room.getInfos();
            Jedis jedis = JedisUtil.getJedis();

            // 首先确定当前回合的玩家方位
            Direction cur = room.findCurAroundUser();
            if(cur == null) {
                throw new NullPointerException("当前玩家回合不能为空！");
            }

            // 确定其他玩家是否有特殊事件发生
            // 是否发生 事件冲突
            // 有两种情况冲突（1. hu, pong  2. hu, gang ）
            PriorityQueue<PlayGameInfo> queue = new PriorityQueue<>((o1, o2) -> o2.getOper() - o1.getOper());
            for (PlayGameInfo info : infos) {
                // 获取操作的值
                int oper = info.getOper();
                int res = info.getRes();
                if(res != 0 && oper != 0) {
                    queue.offer(info);
                }
            }

            if(queue.size() > 0) {
                if(queue.peek().getOper() == GameEvent.Hu.intValue()) {
                    // 后面的操作只能是胡牌才能生效
                    while (!queue.isEmpty()) {
                        PlayGameInfo poll = queue.poll();

                        if(poll.getOper() == GameEvent.Hu.intValue()) {
                            Channel channel = ServerCache.getChannel(poll.getUserId());
                            AuthResponseMessage response = new AuthResponseMessage();
                            poll.setEvent(GameEvent.Hu);
                            ResponseUtil.responseBuildFactory(response, poll, 200, Event.SPCIALEVENT, "事件触发成功！", true);
                            channel.writeAndFlush(response);
                        }

                    }
                }else {
                    PlayGameInfo poll = queue.poll();
                    Channel channel = ServerCache.getChannel(poll.getUserId());
                    AuthResponseMessage response = new AuthResponseMessage();
                    poll.setEvent(GameEvent.value(poll.getOper()));
                    if(queue.size() != 1) {
                        ResponseUtil.responseBuildFactory(response, poll, 500, Event.SPCIALEVENT, "事件触发失败！", false);
                        channel.writeAndFlush(response);
                        // 如果 优先队列中第一个元素不为胡事件，则队列中只能有一个元素
                        throw new ArithmeticException("事件发生冲突错误");
                    }

                    ResponseUtil.responseBuildFactory(response, poll, 200, Event.SPCIALEVENT, "事件触发成功！", true);
                    channel.writeAndFlush(response);
                }
            }

            HashSet<GameUser> players = room.getPlayers();

            for (GameUser player : players) {
                jedis.set("oper_event:" + player.getUserId(), "0");
            }
        });

    }

}
