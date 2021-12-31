package ds.guang.majing.common.timer;


/**
 *
 * 定时任务都必须实现该接口
 *
 * @author guangmingdexin
 */
public interface DsTimerTask {

    /**
     *
     * 定时任务执行方法
     *
     * @param timeout 操作定时任务的对象
     * @throws Exception
     */
    void run(DsTimeout timeout) throws Exception;
}
