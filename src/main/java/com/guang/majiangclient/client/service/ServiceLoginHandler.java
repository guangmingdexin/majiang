package com.guang.majiangclient.client.service;

import com.guang.majiangclient.client.GameClient;
import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.message.LoginRequestMessage;

import java.util.concurrent.Callable;

/**
 * @ClassName ServiceBusinessHandler
 * @Description 登陆
 * @Author guangmingdexin
 * @Date 2021/4/17 13:24
 * @Version 1.0
 **/
public class ServiceLoginHandler extends ServiceHandler {

    public ServiceLoginHandler(Event event) {
        super(event);
    }

    @Override
    public void handle(Task task) {
        Event event = task.getEvent();
        if(event == Event.LOGIN) {
            User data = (User)task.getData();
            // 封装 data
            LoginRequestMessage request = new LoginRequestMessage(data);

            GameClient.getChannel().writeAndFlush(request);
        }else {
            System.out.println("无法处理该事件！");
        }
    }

    @Override
    public void handle(Runnable task) {

    }

    @Override
    public void handle(Callable task) {
        // 判断自己是否可以处理
        if(event == Event.LOGIN) {
            //

        }else {
            // 如果当前处理器已经为最后一个节点
            // 打印信息
            if(nextHandler != null) {
                nextHandler.handle(task);
            }
            System.out.println("没有处理器可以处理这个任务！");
        }
    }
}
