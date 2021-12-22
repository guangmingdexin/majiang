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

    private AtomicBoolean isStart = new AtomicBoolean();

    private AtomicBoolean isValid = new AtomicBoolean(true);

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

    public MatchPoolImpl(int limit, int playerCount) {
        this.limit = limit;
        this.playerCount = playerCount;
    }

    public MatchPoolImpl() {
        this.limit = DEFAULT_LIMIT_CAPACITY;
        this.playerCount = 4;
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
        if(isValid.get() && isStart.get()) {
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
        if(deque.isEmpty()) {
            return false;
        }
        return deque.remove(player);
    }

    @Override
    public Future<List<Player>> match() {

        Future<List<Player>> matchResult = schedule.submit(() -> {
            for (; ; ) {
                if (deque.size() < playerCount) {
                    Thread.sleep(1000);
                } else {
                    List<Player> players = new ArrayList<>();
                    players.add(deque.poll());
                    return players;
                }
            }
        });
        return matchResult;
    }
}
