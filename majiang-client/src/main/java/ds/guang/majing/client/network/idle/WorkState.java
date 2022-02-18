package ds.guang.majing.client.network.idle;

import ds.guang.majing.common.timer.DsWheelTimer;
import io.netty.util.HashedWheelTimer;

import java.util.concurrent.*;

/**
 *
 * 业务线程 - 业务处理，定时任务处理
 *
 * @author guangyong.deng
 * @date 2022-02-17 11:05
 */
public class WorkState extends Thread  {


    public WorkState(String name) {
        super(name);
        this.taskQueue = new LinkedBlockingDeque<>(128);
        // 启动线程
        start();
    }


    // 如果希望保持 socket 的线程安全，则只能对 socket 加锁了

    /**
     *  定时器，默认是 512个刻度  100ms
     */
    static DsWheelTimer wheelTimer = new DsWheelTimer(100, TimeUnit.MILLISECONDS, 512);


    /**
     * 定时心跳处理 - 读空闲
     */
    public static IdleHandler idleHandler = new IdleHandler(3);


    public static final Object LOCK = new Object();

    /**
     *  任务阻塞队列
     */
    private BlockingDeque<Runnable> taskQueue;



    public void runAsync(Runnable runnable) {
        runAsync(runnable, (r, executor) -> {
            throw new RejectedExecutionException("Task " + r.toString());
        });
    }

    public void runAsync(Runnable runnable, RejectedExecutionHandler reject) {
        // 设置最大任务数量
        if(taskQueue.size() >= 128) {
            reject.rejectedExecution(runnable, null);
        }
        taskQueue.offer(runnable);
    }

    @Override
    public void run() {

        while (true) {

            synchronized (LOCK) {
                if(taskQueue.isEmpty()) {
                    // 进入等待？
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    // 执行任务
                    Runnable r = taskQueue.poll();
                    r.run();
                }
            }

        }
    }

}
