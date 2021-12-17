package ds.guang.majing.common.event;

/**
 *
 * @author guangyong.deng
 * @date 2021-12-08 15:46
 */
public abstract class AbstractEvent<R, T> implements Event<R, T> {

    protected R id;

    protected Object data;

    protected String type;

    public AbstractEvent(R id, String type) {
        this.id = id;
        this.type = type;
    }

    public R getId() {
        return id;
    }

    public AbstractEvent setId(R id) {
        this.id = id;
        return this;
    }

    public Object getData() {
        return data;
    }

    public AbstractEvent setData(Object data) {
        this.data = data;
        return this;
    }

    public String getType() {
        return type;
    }

    public AbstractEvent setType(String type) {
        this.type = type;
        return this;
    }
}
