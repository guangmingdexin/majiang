package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.*;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.CommonUtil;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangserver.game.PlayGameHandCardsInfo;
import com.guang.majiangserver.game.PlayGameTask;
import com.guang.majiangserver.game.ServerGameLog;
import com.guang.majiangserver.util.ServerCache;
import com.guang.majiangserver.util.ResponseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName RandomMatchAction
 * @Description 随机匹配
 * @Author guangmingdexin
 * @Date 2021/4/21 15:23
 * @Version 1.0
 **/
@Action
public class RandomMatchAction implements ServerAction<RandomMatchRequestMessage, AuthResponseMessage>{

    // 匹配池
    public static ConcurrentHashMap<Long, GameUser> pool = new ConcurrentHashMap<>();

    // 玩家需要匹配的个数
    private static int NEED_MATCH_PLAYER_COUNT = 4;

    // 匹配线程
    private static ScheduledExecutorService shedule =  Executors.newSingleThreadScheduledExecutor();

    // 房间号
    private static AtomicLong roomId = new AtomicLong(0);

    // 房间信息
    private static  ConcurrentHashMap<Long, Room> roomInfos = new ConcurrentHashMap<>();

    private static ChannelGroup channelGroup;

    static {
        shedule.scheduleWithFixedDelay(() -> {
            //System.out.println("开始匹配！");
            try {
                match(pool);
            }catch (Exception e) {
                System.out.println("出现异常");
                e.printStackTrace();
            }
          // System.out.println("pool: " + pool.size() + " priority: " + priority.size() + " tiem: " + System.currentTimeMillis());
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, RandomMatchRequestMessage request, AuthResponseMessage response) {
        // 1.用户点击随机匹配
        // 2. 客户端发送事件消息，
        // 3. 服务端 发送游戏启动信号消息（进行随机匹配，将其他玩家信息发送给各个客户端）
        // 4. 客户端收到回复（加载本地游戏资源，加载远程信息资源【其他玩家的姓名，头像，位置】）
        // 5. 客户端准备完毕，发送游戏开始信号
        // 6. 当四位玩家 都准备完毕，进入游戏阶段
        // 7. 第一阶段 发牌（服务器将手牌信息发送给客户端，可以多线程进行不需要回复）
        // 8. 第二阶段 摸牌 - 出牌（客户端收到之后需要发送确认信号，再进行下一步）
        GameInfoRequest gameInfo = request.getGameInfo();
        GameUser user = gameInfo.getUser();
        PlayGameInfo playGameInfo = gameInfo.getInfo();
        GameEvent event = playGameInfo.getEvent();
        channelGroup = group;
        long roomId = playGameInfo.getRoomId();
        Room room = roomInfos.get(roomId);
        if(event == GameEvent.InitialGame) {
            // 当玩家人数大于 4 个的时候
            // 首先创建一个房间，将匹配好的玩家加入到房间
            putMatchPool(user.getUserId(), user);
            ServerCache.add(user.getUserId(), ctx.channel());
        }else if(event == GameEvent.StartGame) {
            // 需要一个房间的所有玩家都发送成功后，开始下一阶段
            // 启动一个任务，专门用来处理下一阶段
            roomId = playGameInfo.getRoomId();
            room = roomInfos.get(roomId);
            room.setGameEvent(event);
            // 更新玩家玩家状态

            // TODO 可以上锁 避免同步问题
            AtomicInteger waitNum = room.getWaitNum();
            // 准备玩家减一
            if(waitNum.get() < 0) {
                throw new IllegalArgumentException("线程问题！");
            }
            if(waitNum.decrementAndGet() == 0) {
                // 执行任务
                room.getWaitNum().compareAndSet(0, 4);
                PlayGameHandCardsInfo info = new PlayGameHandCardsInfo();
                room.setGameInfos(info);
                PlayGameTask.addTakeOutCardsTask(info, NEED_MATCH_PLAYER_COUNT, room);
            }
        }else if(event == GameEvent.TakeOutCard) {
            // 校验 用户出的牌是否正确
            // 校验 是否该用户出牌
            // 需要在房间信息中保留应该出牌的玩家方向
            // 玩家有特殊事件时不能出牌
            System.out.println("准备出牌：" + playGameInfo.getValue());
            HashSet<GameUser> players = room.getPlayers();
            for (GameUser player : players) {
                if(!Objects.equals(JedisUtil.get("oper_event:" + player.getUserId()), "0")) {
                    // 还有特殊事件未处理，无法进行下一步
                    return;
                }
            }
            long userId = playGameInfo.getUserId();
            int value = playGameInfo.getValue();
            GameUser gameUser = room.findGameUser(userId);
            List<Integer> useCards = gameUser.getGameInfoCard().getUseCards();
            List<Integer> takeOutCards = gameUser.getGameInfoCard().getTakeOutCards();
            int i = useCards.indexOf(value);
            System.out.println("useCards: " + useCards);
            System.out.println("value: " + value);
            // 需要即时的更新回合切换信息
            if(i != -1 && room.getAround() == gameUser.getDirection()) {
               useCards.remove(i);
               // 向其他玩家发送信息
                takeOutCards.add(value);
                PlayGameInfo info = new PlayGameInfo(roomId, -1, value, gameUser.getDirection(),
                        GameEvent.TakeOutCard, 0, 0, false);
                PlayGameTask.addTakeOutCardTask(userId, info, room);

                ServerGameLog log = ServerCache.readLog(roomId);
                log.offer(playGameInfo);

            }else {
                System.out.println("curDir：" + gameUser.getDirection());
                throw new NullPointerException("玩家不能出这张牌！");
            }
        }else if(event == GameEvent.AckEvent) {

            CommonUtil.checkSpecialEvent(playGameInfo);
            // TODO 一个玩家可能点击多次事件（通过数据库记录状态）
            String operEvent = JedisUtil.get("oper_event:" + playGameInfo.getUserId());
            if(playGameInfo.isAck()) {
                System.out.println("接收特殊事件..." + room.getWaitNum().get() + " " + ctx.channel());
                System.out.println("ackevent: " + playGameInfo);
                // 设置 operEvent 为 1
               JedisUtil.set("oper_event:" + playGameInfo.getUserId(), "2");
            }else {
                JedisUtil.set("oper_event:" + playGameInfo.getUserId(), "0");
            }

            if("2".equals(operEvent)) {
                // 表明 该玩家本次事件已经选择
                return;
            }
            // TODO 存在线程安全问题
            AtomicInteger waitNum = room.getWaitNum();
            synchronized (room) {
                LinkedList<PlayGameInfo> values = room.getSpecialEventInfos();
                if(values == null) {
                    values = new LinkedList<>();
                }
                values.offer(playGameInfo);
                room.setSpecialEventInfos(values);
            }
            // 准备玩家减一
            if(waitNum.get() < 0) {
                throw new IllegalArgumentException("线程问题！");
            }
            if(waitNum.decrementAndGet() == 0) {
                // 执行任务
                room.getWaitNum().compareAndSet(0, 4);
                PlayGameTask.addSpcialTask(room);
            }
        }else if(event == GameEvent.AckAround) {
            // 分为两种情况
            // 1. 都没有特殊事件发生，自动切换到下一个回合
            // 2. 发生特殊事件，由客户端主动请求切换到下一个回合
            // 3. 无论有无特殊事件，必须是出牌之后才能切换回合（胡牌事件除外！）
            HashSet<GameUser> players = room.getPlayers();
            JedisUtil.set("oper_event:" + playGameInfo.getUserId(), "0");
            // 1. pong
            for (GameUser player : players) {
                if (!"0".equals(JedisUtil.get("oper_event:" + player.getUserId()))) {
                    // 还没有准备好
                    return;
                }

            }
            PlayGameTask.nextAround(room, playGameInfo, true);
        }
    }

    private static void match(ConcurrentHashMap<Long, GameUser> pool) {

        // 通过减少匹配算法的复杂度，即最先等待的优先匹配
        HashSet<GameUser> matchPoolPlayer = new HashSet<>();
        // 先获取等待时间最长的 4 个玩家，出队
        int curMatchIndex = 0;
        // 进行参数校验
        if(pool.size() >= NEED_MATCH_PLAYER_COUNT) {
            PriorityQueue<GameUser> priority = new PriorityQueue<>(
                    (o1, o2) -> (int) (o2.getStartMatchTime() - o1.getStartMatchTime()));
            pool.forEach((aLong, gameUser) -> {
                priority.offer(gameUser);
            });
            while (curMatchIndex < NEED_MATCH_PLAYER_COUNT && !priority.isEmpty()) {
                GameUser m = priority.poll();
                matchPoolPlayer.add(m);
                // 从匹配池中移出
                removeMatchPool(m.getUserId());
                curMatchIndex ++;
            }
            // 再次进行校验
            if(curMatchIndex < NEED_MATCH_PLAYER_COUNT) {
                throw new IllegalArgumentException("出现线程问题！赶紧解决");
            }
            // 匹配成功
            // 服务器通知客户端 同时将其余玩家信息发送给客户端
            matchSuccess(matchPoolPlayer);
        }
    }

    public static void putMatchPool(Long userId, GameUser gameUser) {
        pool.put(userId, gameUser);
    }

    public  static void removeMatchPool(Long userId) {
        pool.remove(userId);
    }

    private static void matchSuccess(HashSet<GameUser> matchPoolPlayer) {
        // 设置房间号
        long id = roomId.incrementAndGet();
        Room room = new Room(id, matchPoolPlayer, GameEvent.InitialGame, new AtomicInteger(NEED_MATCH_PLAYER_COUNT));
        roomInfos.put(id, room);
        ServerCache.writeLog(id, new ServerGameLog());
        // 设置玩家位置
        Direction[] directions = new Direction[]{Direction.UNDER, Direction.ABOVE, Direction.LEFT, Direction.RIGHT};
        int i = 0;
        for (GameUser player : matchPoolPlayer) {
            player.setEndMatchTime(System.currentTimeMillis());
            // 加载玩家头像
            player.setBase64(ServerCache.getAvatar(player.getUserId()));
            // 设置玩家位置
            player.setDirection(directions[i++]);
        }

        for (GameUser player : matchPoolPlayer) {
            // 发送匹配成功消息
            // 这个 channel 必须是 服务器的 Channel
            Channel channel = ServerCache.getChannel(player.getUserId());
            AuthResponseMessage response = new AuthResponseMessage();
            ResponseUtil.responseBuildFactory(response, room, 200, Event.RANDOMGAME, "游戏玩家匹配成功", true);
            channel.writeAndFlush(response);
        }
    }

}
