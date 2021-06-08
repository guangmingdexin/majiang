package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.event.SpecialBuildEvent;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName SpecialEventAction
 * @Author guangmingdexin
 * @Date 2021/6/8 9:35
 * @Version 1.0
 **/
@Action(event = Event.SPCIALEVENT)
public class SpecialEventAction implements ClientAction {

    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        System.out.println("执行客户端事件响应！");
        AuthResponse response = message.getResponse();
        Service center = ConfigOperation.getCenter();
        center.submit(new SpecialBuildEvent(response));
    }
}
