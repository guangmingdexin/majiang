package ds.guang.majing.client;

import java.util.concurrent.*;

/**
 * @ClassName ExtendedExecutor
 * @Author guangmingdexin
 * @Date 2021/5/27 15:04
 * @Version 1.0
 **/
public class ExtendedExecutor extends ThreadPoolExecutor {

    public ExtendedExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * 线程池处理异常的任务逻辑
     *
     * try {
     *     task.run();
     * }catch(...) {
     *     // 重新抛出异常
     *     // 将异常传递到 afterExecute 任务
     * }finally {
     *
     *     afterExecute(r, t);
     * }
     * 解决方案 ：重写 afterExecute 方法
     *
     *
     * @param r 任务 task
     * @param t 异常对象
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Object result = ((Future<?>) r).get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
             System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }

}
