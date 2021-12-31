package ds.guang.majiang.server.pool;

import ds.guang.majiang.server.exception.MaxCapacityPoolException;
import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.exception.DsBasicException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    private Deque<Player> deque = new LinkedBlockingDeque<>();

    private final int DEFAULT_LIMIT_CAPACITY = 10240;


    public MatchPoolImpl() {
        this.limit = DEFAULT_LIMIT_CAPACITY;
        this.playerCount = 2;
    }


    /**
     * 记录所有玩家开始匹配的时间
     */
    private Map<String, Long> startMatchMap = new ConcurrentHashMap<>();

    /**
     * 会不会无限创建线程
     * 会不会出现 oom
     */
    private ExecutorService schedule = new ThreadPoolExecutor(1,
            1,
            0L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024),
            new ThreadFactory() {
               final AtomicInteger i = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "match-pool-thread-" + i.get());
                }
            });


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
    public Future<List<Player>> match() {

        if(!isValid() || !isStart()) {
            throw new DsBasicException("加入游戏匹配池失败！");
        }
        System.out.println("当前处理匹配线程---" + Thread.currentThread().getName() + "当前的线程池对象... " + this + playerCount);

        Future<List<Player>> matchResult = schedule.submit(() -> {
            System.out.println("开始匹配一次：" + Thread.currentThread().getName());
            for (; ; ) {
                if (deque.size() < playerCount) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(10000);
                    List<Player> players = new ArrayList<>(4);
                    while (!deque.isEmpty()) {
                        players.add(deque.poll());
                    }
                   // Thread.sleep(10000);
                    return players;
                }
            }
        });
        return matchResult;
    }
}
