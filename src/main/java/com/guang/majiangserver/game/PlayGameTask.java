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
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

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


    private static void takeOutCards(PlayGameHandCardsInfo info, int needPlayNum, Room room) {

        Set<GameUser> players = room.getPlayers();
        // 确定谁是庄家
        int r = new Random().nextInt(needPlayNum);
        Direction marker = Direction.valueOf(r);
        // 首先设置默认玩家回合
        info.setMarker(marker);
        room.setAround(marker);
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
                    for (int i = 0; i < 4 && gameInfoCard.getUseCards().size() < maxHandCard; i++) {
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
    public static void addTakeOutCardsTask(PlayGameHandCardsInfo info, int needPlayNum, Room room) {
        TASK_POOL.execute(() -> {
            takeOutCards(info, needPlayNum, room);
            // 生成一个任务
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
                            res = mask & GameEvent.Gang3.intValue();
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
                room.setRes(0);
                room.setSpecialEventInfos(null);
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
                    int mask = 255;
                    int res = 0;
                    System.out.println("takeOut-useCards: " + useCards);
                    System.out.println("value: " + info.getValue());
                    int count = Algorithm.sortCountArr(useCards, info.getValue());
                    if(count == 2) {
                        res = mask & GameEvent.Pong.intValue();
                    }else if(count == 3) {
                        res = mask & GameEvent.Pong.intValue() & GameEvent.Gang2.intValue();
                    }
                    List<Integer> temp = new ArrayList<>(useCards);
                    Algorithm.sortInsert(temp, info.getValue());
                    if(Algorithm.isHu(temp)) {
                        res = mask & GameEvent.Hu.intValue();
                    }

                    if(res != 0) {
                        // 设置标志位
                        JedisUtil.set("oper_event:" + player.getUserId(), "1");
                    }

                    load = (PlayGameInfo) ClassUtil.copyObj(info);
                    load.setRes(res);

                }
                load.setUserId(player.getUserId());
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
            List<PlayGameInfo> infos = room.getSpecialEventInfos();

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

            if(!queue.isEmpty()) {
                ServerGameLog log = ServerCache.readLog(room.getRoomId());
                // TODO 更新玩家棋牌信息
                int count = 0;
                if(queue.peek().getOper() == GameEvent.Hu.intValue()) {
                    // 后面的操作只能是胡牌才能生效
                    List<PlayGameInfo> infoList = new ArrayList<>();
                    while (!queue.isEmpty()) {
                        PlayGameInfo poll = queue.poll();
                        if(poll.getOper() == GameEvent.Hu.intValue()) {
                            count ++;
                            infoList.add(poll);
                            poll.setEvent(GameEvent.Hu);
                            Channel channel = ServerCache.getChannel(poll.getUserId());
                            AuthResponseMessage response = new AuthResponseMessage();
                            ResponseUtil.responseBuildFactory(response, poll, 200, Event.SPCIALEVENT, "事件触发成功！", true);
                            channel.writeAndFlush(response);
                            log.offer(poll);
                        }else {
                            JedisUtil.set("oper_event:" + poll.getUserId(), "0");
                        }
                    }
                    if(count == 2) {
                        infoList.forEach(info -> info.setEvent(GameEvent.Hu2));
                        System.out.println("infoList: " + infoList);
                    }else if(count == 3) {
                        infoList.forEach(info -> info.setEvent(GameEvent.Hu3));
                        // TODO 游戏结束
                    }
                    infoList = null;
                }else {
                    System.out.println("开始执行特殊事件任务！如果存在！");
                    Channel channel = ServerCache.getChannel(queue.peek().getUserId());
                    AuthResponseMessage response = new AuthResponseMessage();

                    PlayGameInfo poll = queue.poll();

                    if(poll == null || (!queue.isEmpty())) {
                        ResponseUtil.responseBuildFactory(response, poll, 500, Event.SPCIALEVENT, "事件触发失败！", false);
                        channel.writeAndFlush(response);
                        // 如果 优先队列中第一个元素不为胡事件，则队列中只能有一个元素
                        throw new ArithmeticException("事件发生冲突错误");
                    }

                    // TODO 修改玩家分数（pong + 1 被 pong -1）
                    if(poll.getOper() == GameEvent.Pong.intValue()) {
                        // 1.首先修改 redis
                        // 2. 如果修改失败，直接修改数据库，读取数据库数据
                        // 3. 需要一个定时队列进行定时执行任务(即Redis 和 MySQL 进行同步)
                        Direction around = room.getAround();
                        GameUser gameUser = room.findGameUser(around);

                    }

                    poll.setEvent(GameEvent.value(poll.getOper()));
                    ResponseUtil.responseBuildFactory(response, poll, 200, Event.SPCIALEVENT, "事件触发成功！", true);
                    channel.writeAndFlush(response);
                    // 如果游戏事件为 Ignore 直接忽略，不影响后续操作
                    if(poll.getOper() != GameEvent.Ignore.intValue()) {
                        log.offer(poll);
                    }
                }
            }else {
                // 没有特殊事件，则直接自动切换到下一个回合
                System.out.println("开始执行下一个回合");
                nextAround(room, null,false);
            }

            room.getSpecialEventInfos().clear();
        });

    }

    public static void nextAround(Room room, PlayGameInfo playGameInfo, boolean isSpecialEvent) {

        TASK_POOL.execute(() -> {
            HashSet<GameUser> players = room.getPlayers();

            if (isSpecialEvent) {
                // TODO 需要根据不同的事件进行不同的处理
                // 首先判断所有玩家是否已经准备好
               if(playGameInfo.getOper() == GameEvent.Pong.intValue()) {
                   room.setAround(playGameInfo.getDirection());
                   room.nextAround(playGameInfo.getDirection());
               }
            } else {
                // 准备好了，进行发牌操作
                PlayGameHandCardsInfo gameInfos = room.getGameInfos();
                List<Integer> cards = gameInfos.getCards();
                // 确定下家回合将牌发到玩家手上
                if (cards.size() > 0) {
                    Integer r = cards.remove(0);
                    Direction around = room.getAround();
                    int i = 1;
                    GameUser nextUser;
                    while (true) {
                        Direction next = Direction.valueOf((around.getDirection() + i) % 4);
                        nextUser = room.findGameUser(next);
                        if (!nextUser.getGameInfoCard().isHu() && around != nextUser.getDirection()) {
                            break;
                        }
                        i++;
                    }
                    GameInfoCard gameInfoCard = nextUser.getGameInfoCard();
                    System.out.println("当前回合玩家方向为：" + room.getAround());
                    room.setAround(nextUser.getDirection());
                    room.nextAround(nextUser.getDirection());
                    System.out.println("next Direcition：" + nextUser.getDirection());
                    gameInfoCard.getUseCards().add(r);
                    PlayGameInfo info;
                    int value;
                    for (GameUser player : players) {
                        Channel channel = ServerCache.getChannel(player.getUserId());
                        AuthResponseMessage response = new AuthResponseMessage();
                        value = player.getUserId() == nextUser.getUserId() ? r : 0;
                        info = new PlayGameInfo(room.getRoomId(), player.getUserId(), value, nextUser.getDirection(), GameEvent.TakeCard, 0, 0, false);
                        ResponseUtil.responseBuildFactory(response, info, 200, Event.SPCIALEVENT, "回合切换成功，玩家摸牌！", true);
                        channel.writeAndFlush(response);
                    }
                }
                // 发送游戏结束包
            }

        });

    }
}
