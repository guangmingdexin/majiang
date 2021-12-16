package ds.guang.majing.common.state;

import ds.guang.majing.common.event.Event;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guangyong.deng
 * @date 2021-12-13 11:30
 */
public class Context implements Runnable {

    private State state;

    private Event event;

    private Condition condition;

    private Condition nextCondition;

    private ReentrantLock lock;

    private StateMachine machine;

    private Context prevContext;

    private Context nextContext;

    private volatile boolean mark;

    public Context(State state,
                   Event event,
                   Condition condition,
                   Condition nextCondition,
                   ReentrantLock lock,
                   StateMachine machine,
                   Context prevContext,
                   Context nextContext,
                   boolean mark) {
        this.state = state;
        this.event = event;
        this.nextCondition = nextCondition;
        this.condition = condition;
        this.lock = lock;
        this.machine = machine;
        this.prevContext = prevContext;
        this.nextContext = nextContext;
        this.mark = mark;
    }

    public State getState() {
        return state;
    }

    public Context setState(State state) {
        this.state = state;
        return this;
    }

    @Override
    public void run() {
        for (;;) {
            // 1.首先判断是否为自己的回合？
            lock.lock();
            try {
                if(!mark) {
                    nextCondition.signal();
                    condition.await();
                }
                // 如果是自己的回合，则开始游戏
                // 如何开始游戏？
//                state.onEntry(data -> {
//                    System.out.println(Thread.currentThread().getName() + "开始游戏！" + state.getId());
//                }).entry();
                System.out.println(Thread.currentThread().getName() + "开始游戏！" + state.getId());

                Thread.sleep(1000);

                machine.event(event);

                // 进入下一个状态
                // 触发下一个事件，应该根据
                nextContext.mark = true;
                mark = false;
//                state.onExit(data -> {
//
//                }).exit();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public Context setPrevContext(Context prevContext) {
        this.prevContext = prevContext;
        return this;
    }

    public Context setNextContext(Context nextContext) {
        this.nextContext = nextContext;
        return this;
    }

    public Context setNextCondition(Condition nextCondition) {
        this.nextCondition = nextCondition;
        return this;
    }
}
