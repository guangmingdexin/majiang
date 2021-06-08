package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.event.RegisterEvent;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.util.ConfigOperation;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName RegisterAction
 * @Description 处理服务器回应的消息
 * @Author guangmingdexin
 * @Date 2021/4/19 10:27
 * @Version 1.0
 **/
@Action(event = Event.REGISTER)
public class RegisterAction implements ClientAction {

    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        // 首先判断事件是否正确
        AuthResponse response = message.getResponse();
        Event event = response.getEvent();
        if(event == Event.REGISTER) {
            Service center = ConfigOperation.getCenter();
            center.submit(new RegisterEvent(response.getMsg()));
        }else {
            throw new IllegalArgumentException("事件错误！");
        }
    }
}
