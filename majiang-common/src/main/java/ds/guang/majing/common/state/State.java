package ds.guang.majing.common.state;

import ds.guang.majing.common.event.Event;

/**
 * @author guangyong.deng
 * @date 2021-12-17 14:20
 */
public interface State<T, E, R> {

   T getId();

  void setId(T id);

   default State<T,E,R> onEvent(final E eventId, final Handler<R> handler) {
        return onEvent(eventId, null, handler);
    }

   default State<T,E,R> onEvent(final E eventId, final T nextState) {
       return onEvent(eventId, nextState, null);
   }

    State<T,E,R> onEvent(final E eventId, final T nextState, final Handler<R> handler);

    State<T,E,R> onEntry(final Handler handler);

    State<T,E,R> onExit(final Handler handler);

    void entry(Object data);

    void exit();

    R handle(Event<E> event);

    interface Notify<T> {
        void nextState(T nextState, Object data);
    }


    /**
     * 事件处理
     */
    interface Handler<R> {
        /**
         * 处理方法实现
         * @param data 事件参数
         */
        R handle(Object data);
    }

    void setNotify(Notify<T> notify);

}
