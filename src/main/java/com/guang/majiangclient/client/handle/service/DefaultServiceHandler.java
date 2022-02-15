package com.guang.majiangclient.client.handle.service;

import com.guang.majiangclient.client.GameClient;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.annotation.Package;
import com.guang.majiangclient.client.handle.task.Task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

/**
 * @ClassName DefaultServiceHandler
 * @Author guangmingdexin
 * @Date 2021/4/22 9:29
 * @Version 1.0
 **/
public class DefaultServiceHandler extends ServiceHandler {

    public DefaultServiceHandler() {}

    @Override
    public void handle(Task task, Class<?> request, Event event) {
        // 首先获取 service 上是否有 响应注解
        Package pa = request.getAnnotation(Package.class);
        if(pa == null || task.getEvent() != event) {
            next(task, request, event);
            return;
        }

        // 判断当前处理器是否能够处理该事件
        // 构造 请求信息包准备发送
        Object data = task.getData();
        try {
            Constructor<?> constructor = request.getConstructor(data.getClass());
            // 获取参数个数 以及 参数类型
            int paramCount = constructor.getParameterCount();
            Class<?> paramClass = constructor.getParameterTypes()[0];
            if(paramCount != 1 && !paramClass.isAssignableFrom(request)) {
                next(task, request, event);
            }
            Object o = constructor.newInstance(data);
            // 发送信息
            GameClient.getChannel().writeAndFlush(o);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            next(task, request, event);
        }
    }

    @Override
    public void handle(Runnable task) {
        Class<?> clazz = task.getClass();
        RunnableEvent annotation = clazz.getAnnotation(RunnableEvent.class);
        if(annotation == null || annotation.value() != Event.UIEVENT) {
            // 主线程自己执行
            task.run();
        }else {
            System.out.println("这是一个 ui 线程任务 交给 ui 处理器执行");
            if(nextHandler != null) {
                nextHandler.handle(task);
            }
        }

    }

    @Override
    public boolean handle(Callable task) {

        return false;
    }
}
