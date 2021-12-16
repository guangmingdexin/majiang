package ds.guang.majing.common.event;

/**
 * @author guangyong.deng
 * @date 2021-12-13 14:11
 */
public class Event<E> {

    public E id;

    public Object data;

    public Event(E id) {
        this.id = id;
        this.data = new Object();
    }

    public Event(E id, Object data) {
        this.id = id;
        this.data = data;
    }

    public E getId() {
        return id;
    }

    public void setId(E id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
