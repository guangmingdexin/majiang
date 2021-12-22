package ds.guang.majing.common.event;

public class Event<E> {

    protected E id;

    protected Object data;


    public Event(E id, Object data) {
        this.id = id;
        this.data = data;
    }


    public E getId() {
        return id;
    }

    public Event<E> setId(E id) {
        this.id = id;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Event<E> setData(Object data) {
        this.data = data;
        return this;
    }
    
}
