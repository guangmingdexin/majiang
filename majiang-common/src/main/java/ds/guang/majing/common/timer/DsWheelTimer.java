package ds.guang.majing.common.timer;

import ds.guang.majing.common.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

import static ds.guang.majing.common.util.DsConstant.OS_NAME_KEY;
import static ds.guang.majing.common.util.DsConstant.OS_WIN_PREFIX;

/**
 * @author guangmingdexin
 */
public class DsWheelTimer implements DsTimer{

    private static final Logger logger = LoggerFactory.getLogger(DsWheelTimer.class);

    private static final int WORKER_STATE_INIT = 0;
    private static final int WORKER_STATE_STARTED = 1;
    private static final int WORKER_STATE_SHUTDOWN = 2;

    /**
     * 时间轮当前状态
     */
    private volatile int workerState;

    private static final AtomicIntegerFieldUpdater<DsWheelTimer> WORKER_STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(DsWheelTimer.class, "workerState");


    /**
     * 当前时间轮的启动时间，提交到该时间轮的定时任务的 deadline 字段值均以该时间戳为起点进行计算
     * 注意这里为啥使用 volatile ?
     */
    private volatile long startTime;

    /**
     *
     */
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);

    /**
     * 时间轮在处理槽中的任务之前，会先处理这两个队列中的任务
     *
     * 缓存外部提交时间轮中的任务
     */
    private final Queue<DsWheelTimeout> timeouts = new LinkedBlockingQueue<>();

    /**
     * TODO 队列是否可以优化一下
     * 取消的任务放置的队列
     * 感觉类似于 Nio 中的 cancelSelectKeys 每次取消都是先标记，在下次循环中,才会正式移除
     * 包括 GC 也是，先将其放置到队列中，下次扫描才会正式移除（学到了！）
     */
    private final Queue<DsWheelTimeout> cancelledTimeouts = new LinkedBlockingQueue<>();

    private final DsWheelBucket[] wheel;

    /**
     * 二进制掩码，快速确定槽中的位置
     */
    private final int mask;

    /**
     * 时间指针每次加 1 所代表的实际时间，单位为纳秒
     */
    private final long tickDuration;

    /**
     * 当前时间轮剩余的定时任务总数
     */
    private final AtomicLong pendingTimeouts = new AtomicLong(0);

    /**
     * 最大的定时任务总数
     */
    private final long maxPendingTimeouts;

    /**
     * 时间轮内部真正执行定时任务的线程
     */
    private final Thread workerThread;
    private final Worker worker = new Worker();


    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();
    /**
     * 实例对象创建的最大值
     */
    private static final int INSTANCE_COUNT_LIMIT = 64;

    private static final AtomicBoolean WARNED_TOO_MANY_INSTANCES = new AtomicBoolean();

    public DsWheelTimer() {
        this(Executors.defaultThreadFactory());
    }

    public DsWheelTimer(long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
    }

    public DsWheelTimer(ThreadFactory threadFactory) {
        this(threadFactory, 100, TimeUnit.MILLISECONDS);
    }

    public DsWheelTimer(
            ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
        this(threadFactory, tickDuration, unit, 512);
    }

    public DsWheelTimer(
            ThreadFactory threadFactory,
            long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(threadFactory, tickDuration, unit, ticksPerWheel, -1);
    }

    /**
     * @param threadFactory 线程工厂
     * @param tickDuration 单位时间长度
     * @param unit 时间单位
     * @param ticksPerWheel 时间轮大小
     * @param maxPendingTimeouts 最大定时任务大小
     */
    public DsWheelTimer(
            ThreadFactory threadFactory,
            long tickDuration, TimeUnit unit, int ticksPerWheel,
            long maxPendingTimeouts) {

        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }

        // Normalize ticksPerWheel to power of two and initialize the wheel.
        wheel = createWheel(ticksPerWheel);
        mask = wheel.length - 1;

        // Convert tickDuration to nanos.
        this.tickDuration = unit.toNanos(tickDuration);

        // Prevent overflow.
        if (this.tickDuration >= Long.MAX_VALUE / wheel.length) {
            throw new IllegalArgumentException(String.format(
                    "tickDuration: %d (expected: 0 < tickDuration in nanos < %d",
                    tickDuration, Long.MAX_VALUE / wheel.length));
        }
        workerThread = threadFactory.newThread(worker);

        this.maxPendingTimeouts = maxPendingTimeouts;

        if (INSTANCE_COUNTER.incrementAndGet() > INSTANCE_COUNT_LIMIT &&
                WARNED_TOO_MANY_INSTANCES.compareAndSet(false, true)) {
            reportTooManyInstances();
        }
    }


    /**
     * 确定时间轮的 startTime 字段
     * 启动 workerThread 线程，开始执行 worker 任务
     */
    @Override
    public DsTimeout newTimeout(DsTimerTask task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        // 定时任务数加一
        long pendingTimeoutsCount = pendingTimeouts.incrementAndGet();

        if (maxPendingTimeouts > 0 && pendingTimeoutsCount > maxPendingTimeouts) {
            pendingTimeouts.decrementAndGet();
            throw new RejectedExecutionException("Number of pending timeouts ("
                    + pendingTimeoutsCount + ") is greater than or equal to maximum allowed pending "
                    + "timeouts (" + maxPendingTimeouts + ")");
        }

        // 启动时间轮
        start();

        // 计算该定时任务的执行时间，并放入对应的槽中
        long deadline = System.nanoTime() + unit.toNanos(delay) - startTime;

        // Guard against overflow.
        if (delay > 0 && deadline < 0) {
            deadline = Long.MAX_VALUE;
        }
        DsWheelTimeout timeout = new DsWheelTimeout(this, task, deadline);
        timeouts.add(timeout);
        return timeout;
    }


    public void start() {
        switch (WORKER_STATE_UPDATER.get(this)) {
            case WORKER_STATE_INIT:
                if (WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_INIT, WORKER_STATE_STARTED)) {
                    workerThread.start();
                }
                break;
            case WORKER_STATE_STARTED:
                break;
            case WORKER_STATE_SHUTDOWN:
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }

        // Wait until the startTime is initialized by the worker.
        while (startTime == 0) {
            try {
                startTimeInitialized.await();
            } catch (InterruptedException ignore) {
                // Ignore - it will be ready very soon.
            }
        }
    }

    @Override
    public Set<DsTimeout> stop() {
        return null;
    }

    @Override
    public boolean isStop() {
        return false;
    }


    /**
     * 作用：
     * 1.时间轮中双向链表的节点，即定时任务 TimerTask 在 DsWheelTimer 中的容器
     * 2.定时任务 TimerTask 提交到 DsWheelTimer 之后返回的句柄（Handle），
     *   用于在时间轮外部查看和控制定时任务
     */
    private static final class DsWheelTimeout implements DsTimeout {


        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;
        // 原子类变量优化使用方法，保证对象中，属性值变化的线程安全性
        private static final AtomicIntegerFieldUpdater<DsWheelTimeout> STATE_UPDATER =
                AtomicIntegerFieldUpdater.newUpdater(DsWheelTimeout.class, "state");


        private final DsWheelTimer timer;

        /**
         * 实际被调度的任务
         */
        private final DsTimerTask task;

        /**
         * 定时任务执行的时间。在创建 DsWheelTimeout 时指定
         * currentTime（创建 DsWheelTimeout 的时间） + delay（任务延迟时间） - startTime（DsWheelTimer 的启动时间）
         */
        private final long deadline;

        private volatile int state = ST_INIT;

        /**
         * 当前任务剩余的时钟周期数。时间轮所能表示的时间长度有限，在任务到期时间与当前时刻的时间差，
         * 超过时间轮单圈能表示时长，就出现套圈，需要该字段值表示剩余的时钟周期
         */
        long remainingRounds;

        DsWheelTimeout next;
        DsWheelTimeout prev;

        /**
         * 管理双向链表的容器
         */
        DsWheelBucket bucket;


        public DsWheelTimeout(DsWheelTimer timer, DsTimerTask task, long deadline) {
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
        }

        @Override
        public DsTimer timer() {
            return timer;
        }

        @Override
        public DsTimerTask task() {
            return task;
        }

        @Override
        public boolean isExpired() {
            return state() == ST_EXPIRED;
        }

        @Override
        public boolean isCancelled() {
            return state() == ST_CANCELLED;
        }

        @Override
        public boolean cancel() {
            // 仅更新状态 下次将其从 bucket 中移除
            if (!compareAndSetState(ST_INIT, ST_CANCELLED)) {
                return false;
            }
            // 如果应该取消任务，我们将其放入另一个队列，该队列将在每个刻度上处理
            timer.cancelledTimeouts.add(this);
            return true;
        }

        public void expire() {
            // 当任务到期 先将状态设置为 EXPIRED
            if (!compareAndSetState(ST_INIT, ST_EXPIRED)) {
                return;
            }

            try {
                // 执行定时任务
                task.run(this);
            } catch (Throwable t) {
                if (logger.isWarnEnabled()) {
                    logger.warn("An exception was thrown by " + DsTimerTask.class.getSimpleName() + '.', t);
                }
            }
        }

        public int state() {
            return state;
        }

        public boolean compareAndSetState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }

        void remove() {
            DsWheelBucket bucket = this.bucket;
            if (bucket != null) {
                bucket.remove(this);
            } else {
                timer.pendingTimeouts.decrementAndGet();
            }
        }
    }


    /**
     * 用于缓存和管理双向链表的容器
     */
    private static final class DsWheelBucket {

        /**
         *  双向链表的头尾节点
         */
        private DsWheelTimeout head;
        private DsWheelTimeout tail;

        /**
         * 插入双向链表
         */
        void addTimeout(DsWheelTimeout timeout) {
            assert timeout.bucket == null;
            timeout.bucket = this;
            if (head == null) {
                head = tail = timeout;
            } else {
                tail.next = timeout;
                timeout.prev = tail;
                tail = timeout;
            }
        }

        /**
         * 移除头节点
         *
         */
        private DsWheelTimeout pollTimeout() {
            DsWheelTimeout head = this.head;
            if (head == null) {
                return null;
            }
            DsWheelTimeout next = head.next;
            if (next == null) {
                tail = this.head = null;
            } else {
                this.head = next;
                next.prev = null;
            }

            // null out prev and next to allow for GC.
            head.next = null;
            head.prev = null;
            head.bucket = null;
            return head;
        }

        public DsWheelTimeout remove(DsWheelTimeout timeout) {
            DsWheelTimeout next = timeout.next;
            // remove timeout that was either processed or cancelled by updating the linked-list
            if (timeout.prev != null) {
                timeout.prev.next = next;
            }
            if (timeout.next != null) {
                timeout.next.prev = timeout.prev;
            }

            if (timeout == head) {
                // if timeout is also the tail we need to adjust the entry too
                if (timeout == tail) {
                    tail = null;
                    head = null;
                } else {
                    head = next;
                }
            } else if (timeout == tail) {
                // if the timeout is the tail modify the tail to be the prev node.
                tail = timeout.prev;
            }
            // null out prev, next and bucket to allow for GC.
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
            timeout.timer.pendingTimeouts.decrementAndGet();
            return next;
        }


        /**
         * 循环调用 pollTimeout() 方法处理整个双向链表，并返回所有未超时或者未被取消的任务
         */
        void clearTimeouts(Set<DsTimeout> set) {
            for (; ; ) {
                DsWheelTimeout timeout = pollTimeout();
                if (timeout == null) {
                    return;
                }
                if (timeout.isExpired() || timeout.isCancelled()) {
                    continue;
                }
                set.add(timeout);
            }
        }


        /**
         * 遍历双向链表中的全部 DsWheelTimeout 节点。
         * 在处理到期的定时任务时，会通过 remove() 方法取出，
         * 并调用其 expire() 方法执行；对于已取消的任务，
         * 通过 remove() 方法取出后直接丢弃；对于未到期的任务，
         * 会将 remainingRounds 字段（剩余时钟周期数）减一
         *
         */
        void expireTimeouts(long deadline) {
            // 获取双向链表头节点
            DsWheelTimeout timeout = head;

            // 遍历双向链表中的全部 DsWheelTimeout 节点
            while (timeout != null) {
                DsWheelTimeout next = timeout.next;
                // 判断定时任务是否应该在这个时钟周期中执行
                if (timeout.remainingRounds <= 0) {
                    // 取出到期任务
                    next = remove(timeout);
                    //  判断任务是否到期
                    if (timeout.deadline <= deadline) {
                        // 执行到期任务
                        timeout.expire();
                    } else {
                        // The timeout was placed into a wrong slot. This should never happen.
                        throw new IllegalStateException(String.format(
                                "timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                } else if (timeout.isCancelled()) {
                    // 判断任务是否被取消
                    next = remove(timeout);
                } else {
                    // 剩余时钟周期数减一
                    timeout.remainingRounds--;
                }
                timeout = next;
            }
        }

        @Override
        public String toString() {
            // 打印每个槽中的所有任务，以及获取到期时间
            DsWheelTimeout cur = head;
            while (cur != null ){
                System.out.print("rounds: " + cur.remainingRounds + "--" + cur.task + "---" + "deadline: " + cur.deadline);
                System.out.println();
                cur = cur.next;
            }
            return null;
        }
    }

    /**
     *
     * 真正执行定时任务的逻辑封装这个 Runnable 对象中
     *  在 Reactor 中设计自定义调度器时，也是差不多的设计，都会对任务作一个增强
     */
    private final class Worker implements Runnable {

        /**
         * 还未执行的定时任务集合？
         */
        private final Set<DsTimeout> unprocessedTimeouts = new HashSet<>();

        /**
         * 时间轮的指针，步长为 1 的单调递增计数器
         */
        private long tick;


        @Override
        public void run() {
            // 初始化时间轮开始时间 ---
           // 时间轮指针转动，时间轮周期开始
            startTime = System.nanoTime();

            if (startTime == 0) {
                //  用 0 做未初始化状态，所以当时间轮开始启动时，需要设置 startTime 的值为非 0
                startTime = 1;
            }

            startTimeInitialized.countDown();

            do {
                // 获取到下一个时间刻度的实际时间
                final long deadline = waitForNextTick();
                if (deadline > 0) {
                    // 计算槽的位置
                    int idx = (int) (tick & mask);
                    processCancelledTasks();
                    DsWheelBucket bucket = wheel[idx];
                    transferTimeoutsToBuckets();
                    // 清理到期的定时任务
                    bucket.expireTimeouts(deadline);
                    // 时间轮转动
                    tick++;
                }
            } while (WORKER_STATE_UPDATER.get(DsWheelTimer.this) == WORKER_STATE_STARTED);
        }

        /**
         *
         * 如果想模拟时钟循环应该怎么做
         *
         * 1.时间指针单位/ 每次时钟前进的刻度 / 一般可以为 1
         *
         * 2.每个时间单位代表的长度 ，1单位代表 1s、10s、100s
         *
         * 3.实际时间 = 时钟启动时间 + （刻度 * 实际长度）
         *
         */
        private long waitForNextTick() {
            // 时钟指针开始转动 ，并计算出指针指向的实际时间
            // 比如：时间 刻度代表的时间长度为 10 秒，那么下一个时间指针指向的实际时间就是 10 秒后
            /**
             *
             * startTime = 3
             * tick = 0  tickDuration = 10  deadline = 10
             *
             * System.nanoTime = 6
             * currentTime = 6 - 3 = 3
             * sleepTimeMs = (10 - 3 + 9) / 10 = 1
             *
             * System.nanoTime = 7
             * currentTime = 4
             */
            long deadline = tickDuration * (tick + 1);

            for (; ; ) {
                // 计算当前时间轮的时间
                final long currentTime = System.nanoTime() - startTime;

                // 判断当前时间是否已经到了下一个刻度了
                // 可以等效为 startTime + deadline - System.nanoTime() 这样好理解些
                // 是为了让算出来的值多 1 毫秒
                // 当线程调用 Thread.sleep 方法的时候，JVM 会进行一个特殊的调用，将中断周期设置为 1ms
                // 所以会有一个额外的花费，需要多沉睡 1ms
                // 按照正常流程来说：比如设定 1ms sleep，则JVM会在 1ms 后唤醒线程
                // 但实际上来说，操作系统会在中断周期的时候才会去检查线程是否应该被唤醒，所以
                // 线程实际沉睡时间 = （sleepTime + interruptTime）
                long sleepTimeMs = (deadline - currentTime + 999999) / 1000000;

                if (sleepTimeMs <= 0) {
                    if (currentTime == Long.MIN_VALUE) {
                        return -Long.MAX_VALUE;
                    } else {
                        return currentTime;
                    }
                }
                // 判断操作系统
                if (isWindows()) {
                    // 不知道 windows 操作系统为什么要这么做？
                    // https://github.com/netty/netty/issues/356
                    // Thread.sleep 依赖操作系统的中断检查 ，也就是操作系统会在每一个中断的时候去检查是否有线程需要唤醒并且提供 CPU 资源
                    // 如果是 windows 的话，中断周期可能是 10ms 或者 15ms
                    sleepTimeMs = sleepTimeMs / 10 * 10;
                }

                try {
                    // 判断到下一个时钟刻度需要沉睡的时间
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException ignored) {
                    // 中断处理也比较重要
                    if (WORKER_STATE_UPDATER.get(DsWheelTimer.this) == WORKER_STATE_SHUTDOWN) {
                        return Long.MIN_VALUE;
                    }
                }
            }
        }

        /**
         * 清理用户主动取消的定时任务，这些定时任务在用户取消时，记录到 cancelledTimeouts 队列中。
         * 在每次指针转动的时候，时间轮都会清理该队列
         */
        private void processCancelledTasks() {
            for (; ; ) {
               DsWheelTimeout timeout = cancelledTimeouts.poll();
                if (timeout == null) {
                    // all processed
                    break;
                }
                try {
                    timeout.remove();
                } catch (Throwable t) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("An exception was thrown while process a cancellation task", t);
                    }
                }
            }
        }

        /**
         * 将缓存在 timeouts 队列中的定时任务转移到时间轮中对应的槽中
         */
        private void transferTimeoutsToBuckets() {
            // transfer only max. 100000 timeouts per tick to prevent a thread to stale the workerThread when it just
            // adds new timeouts in a loop.
            for (int i = 0; i < 100000; i++) {
                DsWheelTimeout timeout = timeouts.poll();
                if (timeout == null) {
                    // all processed
                    break;
                }
                if (timeout.state() == DsWheelTimeout.ST_CANCELLED) {
                    // Was cancelled in the meantime.
                    continue;
                }
                // 这种分层设计借用了 rounds 这个参数，防止出现需要将时间刻度划分比较小
                long calculated = timeout.deadline / tickDuration;
                // 计算需要转多少圈
                // 比如 秒针转一圈就进入 分针，转 60 圈就可以进入时钟刻度
                // 但是缺点仍然存在，每次需要遍历所有槽中的定时任务，并判断 round 来判断是否需要执行
                timeout.remainingRounds = (calculated - tick) / wheel.length;

                // Ensure we don't schedule for past.
                final long ticks = Math.max(calculated, tick);
                int stopIndex = (int) (ticks & mask);

                DsWheelBucket bucket = wheel[stopIndex];
                bucket.addTimeout(timeout);
            }
        }

    }

    private static final boolean IS_OS_WINDOWS = System.getProperty(OS_NAME_KEY, "").toLowerCase(Locale.US).contains(OS_WIN_PREFIX);

    private boolean isWindows() {
        return IS_OS_WINDOWS;
    }

    private static DsWheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException(
                    "ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
        if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException(
                    "ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
        }

        ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
        DsWheelBucket[] wheel = new DsWheelBucket[ticksPerWheel];
        for (int i = 0; i < wheel.length; i++) {
            wheel[i] = new DsWheelBucket();
        }
        return wheel;
    }

    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = ticksPerWheel - 1;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 1;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 2;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 4;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 8;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 16;
        return normalizedTicksPerWheel + 1;
    }

    private static void reportTooManyInstances() {
        String resourceType = ClassUtil.simpleClassName(DsWheelTimer.class);
        logger.error("You are creating too many " + resourceType + " instances. " +
                resourceType + " is a shared resource that must be reused across the JVM," +
                "so that only a few instances are created.");
    }
}
