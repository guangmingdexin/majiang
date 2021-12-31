package ds.guang.majing.common.timer;



import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author guangmingdexin
 */
public interface DsTimer {

    /**
     *
     * 创建一个定时任务
     *
     * @param task 定时任务
     * @param delay 任务延迟时间
     * @param unit 时间单位
     * @return 一个可以操作定时任务的句柄
     */
    DsTimeout newTimeout(DsTimerTask task, long delay, TimeUnit unit);


    /**
     * @return
     */
    Set<DsTimeout> stop();

    /**
     * @return
     */
    boolean isStop();
}
