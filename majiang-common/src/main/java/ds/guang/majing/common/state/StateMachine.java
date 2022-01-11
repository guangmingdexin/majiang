package ds.guang.majing.common.state;


import ds.guang.majing.common.event.Event;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * 状态机
 * @author guangmingdexin
 * @param <T> 状态ID的类型
 * @param <E> 事件ID的类型
 * Created by ygl_h on 2017/7/17.
 */
public class StateMachine<T,E,R extends Result> implements State.Notify<T> {

    private static final String TAG = StateMachine.class.getName();


   // private Logger logger;
    /**
     * 状态列表
     */
    protected List<State<T,E,R>> states;
    /**
     * 初始状态
     */
    protected T initStateId;
    /**
     * 当前状态
     */
    protected State<T,E,R> currentState;

    public StateMachine() {
        this.states = new CopyOnWriteArrayList<>();
       // this.logger = new LogcatLogger();
    }

    /**
     * 设置状态表
     * @param otherStates 其他各状态
     */
    public void registerState(State<T,E,R>... otherStates) {
       // 首先判断是否注册初始状态
        if(states.isEmpty()) {
            System.out.println("don't register initial state, please invoke #registerInitialState");
            throw new ArithmeticException("don't register initial state, please invoke #registerInitialState");
        }
        Collections.addAll(states, otherStates);
        for (State<T,E,R> state : states) {
            state.setNotify(this);
        }
    }

    public void registerInitialState(State<T,E,R> initState) {
        initStateId = initState.getId();
        states.add(initState);
        initState.setNotify(this);
    }

    /**
     * 启动状态机
     */
    public void start() {
        nextState(initStateId, null);
    }

    /**
     * 输入事件
     * @param event 事件
     * @return 是否处理了此事件
     */
    public R event(Event<E> event) {
       // logger.i(TAG, currentState.id + "状态下触发事件："+event.id);
        return currentState.handle(event);
    }

    /**
     * 输入事件
     * @param eventId 事件 ID
     * @param data 数据
     * @return 是否处理了此事件
     */
    public R event(E eventId, Object data) {
        return event(new Event<E>(eventId, data));
    }

    @Override
    public void nextState(T nextStateId, Object data) {

        int loopCount = 0;

        if (currentState != null) {
          //  logger.i(TAG, "状态变化，离开："+currentState.id);
            currentState.exit();
        }
        for (State<T,E,R> state : states) {
            if (state.getId().equals(nextStateId)) {
                currentState = state;
                break;
            }
            loopCount ++;
        }
        if(loopCount == states.size()) {
            // 没有找到下一个状态
            throw new NullPointerException("don't find next state!");
        }

        if (currentState != null) {
           // logger.i(TAG, "状态变化，进入："+currentState.id);
            currentState.entry(data);
        }
    }

    public void setLogger(Logger logger) {
      //  this.logger = logger;
    }

    /**
     *
     * 手动切换状态
     *
     * @param stateId
     * @param data
     */
    public void setCurrentState(T stateId, Object data) {
        Objects.requireNonNull(stateId, "state don't null");
        if(currentState.getId().equals(stateId)) {
            return;
        }
        states.forEach(s -> {
            if(stateId.equals(s.getId())) {
                // 状态发生了切换
                currentState = s;
                currentState.entry(data);
            }
        });

        if(currentState.getId() != stateId) {
            throw new NullPointerException("don't find the next state");
        }

    }


    public State<T, E, R> getCurState() {
        return currentState;
    }
}

