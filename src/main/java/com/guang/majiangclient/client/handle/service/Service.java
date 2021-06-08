package com.guang.majiangclient.client.handle.service;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.handle.task.Task;

import java.util.concurrent.Callable;

/**
 * @ClassName Service
 * @Description 服务抽象接口
 * @Author guangmingdexin
 * @Date 2021/3/25 17:35
 * @Version 1.0
 **/
public interface Service {

    /**
     * 服务处理方法
     * 将 任务提交到 服务中心
     * 服务中心通过注册的服务处理器处理任务
     * 任务 分为 ui 更新任务 服务器客户端任务
     * 可以异步的返回处理结果，也可以同步的执行
     *
     * @param handler 具体的服务处理类
     */
    void register(ServiceHandler handler);


    /**
     * 同步执行 无返回值
     *
     * @param task
     */
    void submit(Runnable task);

    /**
     * 同步执行，带有返回值
     *
     * @param task 任务
     */
    boolean submit(Callable task);

    /**
     * 同步执行 有返回值
     *
     * @param task 任务
     * @param request 请求消息的类型
     */
    void submit(Task task, Class<?> request, Event event);
}
