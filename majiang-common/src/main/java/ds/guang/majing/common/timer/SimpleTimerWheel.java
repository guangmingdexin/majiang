package ds.guang.majing.common.timer;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author guangyong.deng
 * @date 2021-12-30 16:42
 */
public class SimpleTimerWheel {

    /**
     * 一个槽的时间间隔(时间轮最小刻度)
     */
    private long tickMs;

    /**
     * 时间轮大小(槽的个数)
     */
    private int wheelSize;

    /**
     * 一轮的时间跨度
     */
    private long interval;

    /**
     * 下层时间轮
     */
    private volatile SimpleTimerWheel overflowWheel;

    /**
     * 执行任务线程池
     */
    private Executor executor;


    /**
     *  时钟数组
     */
    private Entry[] entries;

//     1. 加入任务 2. 执行任务 3. 删除任务 4. 沿着时间刻度前进
    private int cur;

    public SimpleTimerWheel() {
        // 1h 可以完成的业务每天的 九点
        this.tickMs = 1L;
        this.wheelSize = 24;
        this.entries = new Entry[24];
        executor = Executors.newSingleThreadExecutor();
    }

    public SimpleTimerWheel(Executor executor) {
        this.executor = executor;
    }


    /**
     *
     * 向时间轮中添加任务
     *
     * @param task
     * @param time
     * @param unit
     */
    public void addTask(Task task, int time, TimeUnit unit) {
        // 1.确定这个任务时间在时间轮中的位置 比如 每天的九点执行
        switch (unit) {
            case HOURS:
                Entry entry = new Entry();
                entry.tasks.add(task);
                entries[time] = entry;
                break;
            default:
                System.out.println("其他时间轮还没有完成");

        }
    }

    public void process(long time) {
        // 如何循环这个时间轮

    }

    /**
     *
     * 单个槽，应该有一个任务链表，
     *
     * @param <K>
     * @param <V>
     */
    static class Entry<K, V> {

        /**
         * 单个槽对应的任务
         */
        Deque<Task> tasks = new LinkedList<>();



    }






}
