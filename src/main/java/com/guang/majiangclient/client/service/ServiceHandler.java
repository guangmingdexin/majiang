package com.guang.majiangclient.client.service;

import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.task.Task;

import java.util.concurrent.Callable;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
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
    public abstract void handle(Task task);

    public abstract void handle(Runnable task);

    public abstract void handle(Callable task);

}
