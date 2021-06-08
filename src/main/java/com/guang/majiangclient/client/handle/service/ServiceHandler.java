package com.guang.majiangclient.client.handle.service;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.handle.task.Task;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
@NoArgsConstructor
public abstract class ServiceHandler {

    protected ServiceHandler nextHandler;

    // handler 能够处理的任务类型
    protected Event event;

    public ServiceHandler(Event event) {
        this.event = event;
    }

    /**
     * 服务处理器处理方法，用于处理真正的任务
     * 1. 能够处理任务
     * 2. 能够返回处理结果
     *
     */
    public abstract void handle(Task task, Class<?> request, Event event);

    public abstract void handle(Runnable task);

    public abstract boolean handle(Callable task);

    protected void next(Task task, Class<?> request, Event event) {
        if(nextHandler != null) {
            nextHandler.handle(task, request, event);
        }else {
            System.out.println("没有处理器可以处理这个任务！" + event + " " + request);
        }
    }

}
