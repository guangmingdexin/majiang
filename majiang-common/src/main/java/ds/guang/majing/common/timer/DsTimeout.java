package ds.guang.majing.common.timer;


/**
 * @author guangmingdexin
 */
public interface DsTimeout {

    DsTimer timer();

    /**
     *
     */
    DsTimerTask task();

    /**
     *
     *
     */
    boolean isExpired();

    /**
     *
     *
     */
    boolean isCancelled();

    /**
     *
     */
    boolean cancel();
}
