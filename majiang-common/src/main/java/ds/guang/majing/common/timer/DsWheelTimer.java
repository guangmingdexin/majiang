package ds.guang.majing.common.timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author guangmingdexin
 */
public class DsWheelTimer implements DsTimer{



    private static final int WORKER_STATE_INIT = 0;
    private static final int WORKER_STATE_STARTED = 1;
    private static final int WORKER_STATE_SHUTDOWN = 2;

    /**
     * 时间轮当前状态
     */
    private volatile int workerState;

    /**
     * 当前时间轮的启动时间，提交到该时间轮的定时任务的 deadline 字段值均以该时间戳为起点进行计算
     */
    private volatile long startTime;

    @Override
    public DsTimeout newTimeout(DsTimerTask task, long delay, TimeUnit unit) {
        return null;
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
     * 1.时间轮中双向链表的节点，即定时任务 TimerTask 在 HashedWheelTimer 中的容器
     * 2.定时任务 TimerTask 提交到 HashedWheelTimer 之后返回的句柄（Handle），
     *   用于在时间轮外部查看和控制定时任务
     */
    private static final class DsWheelTimeout implements DsTimeout {


        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;
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
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean cancel() {
            return false;
        }

        public int state() {
            return state;
        }
    }


    private static final class DsWheelBucket {


    }
}
