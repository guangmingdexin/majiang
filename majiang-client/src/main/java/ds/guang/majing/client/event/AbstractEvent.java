package ds.guang.majing.client.event;

/**
 *
 * @author guangyong.deng
 * @date 2021-12-08 15:46
 */
public abstract class AbstractEvent<T> implements Event<T> {

    private String type;

    public AbstractEvent(String type) {
        this.type = type;
    }
}
