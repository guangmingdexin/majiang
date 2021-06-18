package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.event.FriendBuilderEvent;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName FriendAction
 * @Author guangmingdexin
 * @Date 2021/6/17 14:09
 * @Version 1.0
 **/
@Action(event = Event.FRIEND)
public class FriendAction implements ClientAction {

    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        AuthResponse response = message.getResponse();

        Service center = ConfigOperation.getCenter();
        center.submit(new FriendBuilderEvent(response));
    }
}
