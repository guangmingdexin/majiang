package com.guang.majiangclient.client.handle.service;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.handle.task.Task;

import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * @ClassName ServiceCenter
 * @Description 服务处理中心
 * @Author guangmingdexin
 * @Date 2021/3/25 17:39
 * @Version 1.0
 *
 * 内部类构造单例
 **/
public class ServiceCenter implements Service {

    // 使用双向队列,还需要保证多线程安全
    private LinkedList<ServiceHandler> handlers = new LinkedList<>();


    private ServiceCenter() {}

    @Override
    public void register(ServiceHandler handler) {

        ServiceHandler prev;

        if(handlers != null && handlers.size() > 0) {
            prev = handlers.getLast();
            prev.nextHandler = handler;
        }
        assert handlers != null;
        handlers.addLast(handler);
    }

    @Override
    public void submit(Runnable task) {
        ServiceHandler head = handlers.peek();
        if(head == null) {
            throw new NullPointerException("没有服务注册到服务中心，请先注册服务！");
        }
        head.handle(task);
    }

    @Override
    public boolean submit(Callable task) {
        ServiceHandler head = handlers.peek();
        if(head == null) {
            throw new NullPointerException("没有服务注册到服务中心，请先注册服务！");
        }
        return head.handle(task);
    }

    @Override
    public void submit(Task task, Class<?> request, Event event) {
        ServiceHandler head = handlers.peek();
        if(head == null) {
            throw new NullPointerException("没有服务注册到服务中心，请先注册服务！");
        }
        head.handle(task, request, event);
    }

    private static class ServiceCenterHolder {
        private static ServiceCenter center = new ServiceCenter();
    }

    public static ServiceCenter getInstance() {
        return ServiceCenterHolder.center;
    }
}
