package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.event.ExceptionEvent;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.service.Service;
import com.guang.majiangclient.client.util.ConfigOperation;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName ExceptionAction
 * @Description TODO
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
        center.submit(new ExceptionEvent(Event.UIEVENT, response.getMsg()));
    }
}
