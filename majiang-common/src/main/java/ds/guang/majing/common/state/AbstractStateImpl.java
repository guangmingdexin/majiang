package ds.guang.majing.common.state;

import ds.guang.majing.common.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *  游戏状态的公共接口
 * @param <T> 状态ID的类型
 * @param <E> 事件ID的类型
 *
 *
 * @author guangyong.deng
 * @date 2021-12-13 11:17
 */
public abstract class AbstractStateImpl<T, E, R extends Result> implements State<T, E, R> {

    /**
     * 状态ID
     */
    T id;
    /**
     * 通知状态机用回调接口
     */
    Notify<T> notify;
    /**
     * 事件处理映射表
     */
    Map<E, Handler<R>> handlerMap;
    /**
     * 进入事件
     */
    Handler onEntry;
    /**
     * 离开事件
     */
    Handler onExit;


    /**
     * @param id 状态ID
     */
    public AbstractStateImpl(T id) {
        this.id = id;
        handlerMap = new HashMap<>();
    }

    /**
     * 取得本状态ID
     * @return 本状态ID
     */
    @Override
    public T getId() {
        return id;
    }

    /**
     * 设置本状态ID
     * @param id 状态ID
     */
    @Override
    public void setId(T id) {
        this.id = id;
    }

    /**
     * 绑定事件处理
     * @param eventId 事件ID
     * @param handler 处理
     * @return 本对象
     */
    @Override
    public State<T,E,R> onEvent(final E eventId, final Handler<R> handler) {
        handlerMap.put(eventId, handler);
        return this;
    }


    /**
     * 绑定状态跳转事件
     * @param eventId 事件ID
     * @param nextState 要跳转的下一个状态ID
     * @return 本对象
     */
    @Override
    public State<T,E,R> onEvent(final E eventId, final T nextState) {
        return onEvent(eventId, nextState, null);
    }

    
    /**
     * 绑定状态跳转事件处理
     * @param eventId 事件ID
     * @param nextState 要跳转的下一个状态ID
     * @param handler 处理
     * @return 本对象
     */
    @Override
    public State<T,E,R> onEvent(final E eventId, final T nextState, final Handler<R> handler) {

       handlerMap.put(eventId, data -> {
           R  r = null;
           if(handler != null) {
               r = handler.handle(data);
           }
           // 需要通过结果来判断处理事件成功，如果成功，则可以进入下一个状态，否则
           if(r != null && r.success() && nextState != null) {
               System.out.println("进入下一个状态......！");
               notify.nextState(nextState, data);
           }
           return r;
       });
       return this;
    }

    /**
     * 绑定进入本状态的处理
     * @param handler 处理
     * @return 本对象
     */
    @Override
    public State<T,E,R> onEntry(final Handler handler) {
        this.onEntry = handler;
        return this;
    }

    /**
     * 绑定离开本状态时的处理
     * @param handler 处理
     * @return 本对象
     */
    @Override
    public State<T,E,R> onExit(final Handler handler) {
        this.onExit = handler;
        return this;
    }

    @Override
    public void entry(Object data) {
        if (onEntry != null) {
            onEntry.handle(data);
        }
    }

    @Override
    public void exit() {
        if (onExit != null) {
            onExit.handle(new ArrayList<>());
        }
    }

    @Override
    public R handle(Event<E> event) {
        Handler<R> handler = handlerMap.get(event.getId());
        if (handler != null) {
            return handler.handle(event.getData());
        }
        return null;
    }

    @Override
    public void setNotify(Notify<T> notify) {
        this.notify = notify;
    }

    /**
     * 1.希望达成什么效果？
     * 2.怎样达成这个效果？
     *
     * 一：玩家有多个状态，玩家可能在多个状态中不断切换，玩家的状态会受内部因素和外部输入影响
     *     例如：玩家摸牌造成自身状态改变或者其他玩家出牌对其造出改变
     *     状态如何定义
     *     不同状态会出现不同效果
     *     状态之间如何切换
     *     如何管理状态
     *     客户端和服务端的状态如何同步
     *     如何和 JavaFx 系统相搭配
     *
     *
     * @return
     */
    State next() { return null;};
}
