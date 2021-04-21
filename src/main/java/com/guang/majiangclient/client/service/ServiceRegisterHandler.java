package com.guang.majiangclient.client.service;

import com.guang.majiangclient.client.GameClient;
import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.message.RegisterRequestMessage;


import java.util.concurrent.Callable;

/**
 * @ClassName ServiceRegisterHandler
 * @Description 注册 服务处理，将事件消息封装发送给服务器
 * @Author guangmingdexin
 * @Date 2021/4/17 14:21
 * @Version 1.0
 **/
public class ServiceRegisterHandler extends ServiceHandler {

    public ServiceRegisterHandler(Event event) {
        super(event);
    }

    @Override
    public void handle(Task task) {
        Event event = task.getEvent();
        if(event == Event.REGISTER) {
            User data = (User)task.getData();
            // 封装 data
            RegisterRequestMessage request = new RegisterRequestMessage(data);
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

    }
}
