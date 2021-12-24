package ds.guang.majing.common.factory;

import ds.guang.majing.common.state.State;

import java.util.function.Supplier;

/**
 * @author guangyong.deng
 * @date 2021-12-23 9:25
 */
public interface StateStrategy<T, E, R> {


    /**
     *
     * 可以通过注册事件直接创建一个 状态，增加扩展性
     *
     * @param handler 提供的事件处理
     * @param stateId 状态 id
     * @param eventId 事件 id
     * @return state
     */
    State<T, E, R> newState(Supplier<T> stateId, Supplier<E> eventId, Supplier<State.Handler<R>> handler);


    /**
     * @param stateId
     * @return
     */
    State<T, E, R> newState(Supplier<T> stateId);
}
