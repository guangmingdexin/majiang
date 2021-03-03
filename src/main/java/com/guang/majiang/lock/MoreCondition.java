package com.guang.majiang.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName MoreCondition
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/2/28 17:15
 * @Version 1.0
 **/
public final class MoreCondition {

    public final static ReentrantLock lock = new ReentrantLock();

    public final static Condition waitAiRound = lock.newCondition();


    private static volatile boolean isAiRound = false;


}
