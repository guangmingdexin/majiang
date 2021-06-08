package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.event.ExceptionEvent;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.util.ConfigOperation;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName ExceptionAction
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/19 11:04
 * @Version 1.0
 **/
@Action(event = Event.EXCEPTION)
public class ExceptionAction implements ClientAction{
    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        Service center = ConfigOperation.getCenter();
        AuthResponse response = message.getResponse();
        center.submit(new ExceptionEvent(response.getMsg()));
    }
}
