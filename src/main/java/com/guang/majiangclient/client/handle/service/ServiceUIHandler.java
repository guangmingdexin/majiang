package com.guang.majiangclient.client.handle.service;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.handle.task.Task;
import javafx.application.Platform;

import java.util.concurrent.Callable;

/**
 * @ClassName ServiceUIHandler
 * @Description ui 渲染事件处理器
 * @Author guangmingdexin
 * @Date 2021/3/25 18:08
 * @Version 1.0
 **/
public class ServiceUIHandler extends ServiceHandler {

    public ServiceUIHandler(Event event) {
        super(event);
    }

    @Override
    public void handle(Task task, Class<?> request, Event event) {
        next(task, request, event);
    }

    @Override
    public void handle(Runnable task) {

        Class<?> clazz = task.getClass();
        RunnableEvent annotation = clazz.getAnnotation(RunnableEvent.class);

        // 判断自己是否可以处理(通过是否有)
        if(annotation != null && annotation.value() == Event.UIEVENT) {
            // 提交给平台执行
            Platform.runLater(task);
        }else {
            // 如果当前处理器已经为最后一个节点
            // 打印信息
            if(nextHandler != null) {
                nextHandler.handle(task);
            }
            System.out.println("没有处理器可以处理这个任务！");
        }

    }

    @Override
    public boolean handle(Callable task) {

        return false;
    }

}
