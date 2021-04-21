package com.guang.majiang.ai;

import javafx.application.Platform;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName ExcutorThread
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/3 19:49
 * @Version 1.0
 **/
public class Worker extends Thread {

    private LinkedBlockingQueue<Runnable> tasks;

    private Runnable task;

    public Worker(LinkedBlockingQueue<Runnable> tasks) {
        this.tasks = tasks;
    }

    public Worker(Runnable task, LinkedBlockingQueue<Runnable> tasks) {
        this.task = task;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        // 执行任务
        // 1）当 task 还未执行，执行任务
        // 2）当 task 执行完毕，接着从任务队列执行任务
        while (true) {
            while (task != null || (task = tasks.poll()) != null) {
                try {
                    // 提交给平台执行
                    Platform.runLater(task);
                    System.out.println("执行任务： " + task);
                }catch (Exception e) {
                    e.getStackTrace();
                }finally {
                    task = null;
                }
            }
        }
    }
}
