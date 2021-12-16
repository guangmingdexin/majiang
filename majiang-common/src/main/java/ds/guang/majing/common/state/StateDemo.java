package ds.guang.majing.common.state;

import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.event.ThinkEvent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guangyong.deng
 * @date 2021-12-13 11:33
 */
public class StateDemo {


    public static void main(String[] args) {

        /**
         * 优点：
         *      1.代码便于扩展
         *      2.耦合性较低，可以将业务分离出来
         *      3.状态之间便于管理
         * 缺点：
         *      1.代码层次较多，结构较为复杂，不容易理解
         *      2.类太多了
         *
         *
         */
        // 模拟四个玩家，如何进行控制
        StateMachine<String, String, DsResult> machine = new StateMachine<>();

        ThinkState thinkState = new ThinkState();

        ThinkEvent thinkEvent = new ThinkEvent();

        TakeCardState takeCardState = new TakeCardState();

        HandOutCardState handOutCardState = new HandOutCardState();

        machine.registerState(thinkState, takeCardState, handOutCardState);

        machine.start();

        thinkState.onEvent(thinkEvent.getId(), takeCardState.getId(), data -> {
            System.out.println("当前玩家状态是初始化状态！，下一个状态是摸牌状态！");
            return null;
        });

        takeCardState.onEvent(takeCardState.getId(), thinkState.getId(), data -> {
            System.out.println("当前玩家是摸牌状态！，下一个状态是出牌状态！");
            return null;
        });

        ReentrantLock lock = new ReentrantLock();

        Condition c = lock.newCondition();


        Condition c1= lock.newCondition();
        Context p1 = new Context(thinkState, thinkEvent, c1, null,
                lock, machine, null, null,true);

        Condition c2= lock.newCondition();
        Context p2 = new Context(thinkState, thinkEvent, c2, null,
                lock, machine, null, null,false);

        Condition c3= lock.newCondition();
        Context p3 = new Context(thinkState, thinkEvent, c3, null,
                lock, machine, null, null,false);

        Condition c4= lock.newCondition();
        Context p4 = new Context(thinkState, thinkEvent, c4, null,
                lock, machine, null, null,false);

        p1.setPrevContext(p4);
        p1.setNextContext(p2);
        p1.setNextCondition(c2);

        p2.setPrevContext(p1);
        p2.setNextContext(p3);
        p2.setNextCondition(c3);

        p3.setPrevContext(p2);
        p3.setNextContext(p4);
        p3.setNextCondition(c4);

        p4.setPrevContext(p3);
        p4.setNextContext(p1);
        p4.setNextCondition(c1);

        Thread t1 = new Thread(p1, "p1");

        Thread t2 = new Thread(p2, "p2");

        Thread t3 = new Thread(p3, "p3");

        Thread t4 = new Thread(p4, "p4");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

    }
}
