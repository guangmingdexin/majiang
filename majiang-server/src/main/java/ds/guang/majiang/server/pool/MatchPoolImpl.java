package ds.guang.majiang.server.pool;

import ds.guang.majiang.server.exception.MaxCapacityPoolException;
import ds.guang.majiang.server.room.FourRoom;
import ds.guang.majiang.server.room.RoomManager;
import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.exception.DsBasicException;
import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;
import ds.guang.majing.common.timer.DsTimeout;
import ds.guang.majing.common.timer.DsTimerTask;
import ds.guang.majing.common.timer.DsWheelTimer;
import io.netty.channel.Channel;
import io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ds.guang.majing.common.DsConstant.EVENT_PREPARE_ID;
import static ds.guang.majing.common.DsConstant.preRoomInfoPrev;

/**
 * @author guangmingdexin
 */
public class MatchPoolImpl implements MatchPool {

    private AtomicBoolean isStart = new AtomicBoolean(false);

    private AtomicBoolean isValid = new AtomicBoolean(true);

    public static final MatchPool INSTANCE = new MatchPoolImpl();

    /**
     * 单个游戏可以容纳的最大玩家数量
     */
    private int limit;

    /**
     * 玩家人数
     */
    private int playerCount;

    private MpscChunkedArrayQueue<Player> deque;

    private final int DEFAULT_LIMIT_CAPACITY = 10240;


    /**
     * 记录所有玩家开始匹配的时间
     */
    private Map<String, Long> startMatchMap;


    private ThreadFactory defaultThreadFactory =  new ThreadFactory() {
        final AtomicInteger i = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "match-pool-thread-" + i.get());
        }
    };

    Executor schedule = new ThreadPoolExecutor(1,
            1,
            0L,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
            defaultThreadFactory);

    /**
     * 时间轮定时器
     */
    private DsWheelTimer wheelTimer;

    public MatchPoolImpl() {
        this.limit = DEFAULT_LIMIT_CAPACITY;
        this.playerCount = 2;
        this.deque = new MpscChunkedArrayQueue<>(limit);
        this.wheelTimer = new DsWheelTimer(defaultThreadFactory, 1, TimeUnit.SECONDS, 60);
    }


    @Override
    public void start() {
        if(isStart()) {
            return;
        }
        isStart.compareAndSet(false, true);
    }

    @Override
    public boolean isValid() {
        return isValid.get();
    }

    @Override
    public boolean isStart() {
        return isStart.get();
    }

    @Override
    public boolean addPlayer(Player player) {
        if(!isValid() || !isStart()) {
            throw new DsBasicException("加入游戏匹配池失败！");
        }
        if(deque.size() >= limit) {
            isValid.compareAndSet(true, false);
            throw new MaxCapacityPoolException("游戏匹配池已达到最大容量！");
        }
        return deque.offer(player);
    }

    @Override
    public boolean removePlayer(Player player) {
        if(!isValid() || !isStart()) {
            throw new DsBasicException("加入游戏匹配池失败！");
        }
        if(deque.isEmpty()) {
            return false;
        }
        return deque.remove(player);
    }

    @Override
    public void match() {

        if(!isValid() || !isStart()) {
            throw new DsBasicException("加入游戏匹配池失败！");
        }
      //  System.out.println("当前处理匹配线程---" + Thread.currentThread().getName() + "当前的线程池对象... " + this + playerCount);


        DsTimerTask timerTask = timeout -> {

            if (deque.size() >= playerCount) {
                int index = 0;
                Player[] players = new Player[playerCount];
                while (!deque.isEmpty() && index < playerCount) {
                    players[index++] = deque.poll();
                }
                // 获取全局变量
                Cache cache = Cache.getInstance();
                RoomManager manager = RoomManager.getInstance();

                Room room = new FourRoom(playerCount, players);
                for (Player player : players) {
                    // 获取 Channel 输出数据
                    String id = player.getId();
                    Object value = cache.getObject(DsConstant.preUserChanelPrev(id));
                    System.out.println("value: " + value);
                    if (value instanceof Channel) {
                        Channel channel = (Channel) value;
                        // 构造一个 Message 对象
                        DsMessage dsMessage = DsMessage.build(EVENT_PREPARE_ID, id, DsResult.data(room));
                        // 向客户端发送信息
                        // 按理来说这里应该使用异步线程，但是 netty 的特性，会将这次发送
                        // 消息封装为一个任务加入到任务队列中，等待 NioEventLoop 执行，所以
                        // 这里并不会阻塞定时器
                        System.out.println("开始发送消息！");
                        channel.writeAndFlush(dsMessage);
                    }else {
                        throw new RejectedExecutionException("获取通道失败！");
                    }
                    // 将 玩家与房间联系在一起
                    manager.put(preRoomInfoPrev(id), room);
                }
            } else {
                System.out.println("条件不满足！..." + System.currentTimeMillis());
            }
        };
        DsTimeout dsTimeout = wheelTimer.newTimeout(timerTask, 5, TimeUnit.SECONDS);
        System.out.println("expired: " + dsTimeout.isExpired());
    }
}
