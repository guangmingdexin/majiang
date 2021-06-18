package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.PingRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

/**
 * @ClassName PingAction
 * @Author guangmingdexin
 * @Date 2021/6/16 14:41
 * @Version 1.0
 **/
@Action
public class PingAction implements ServerAction<PingRequestMessage, AuthResponseMessage> {

    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, PingRequestMessage request, AuthResponseMessage response) {
        System.out.println("id:" + ctx.channel().id().asShortText() + " -> " + request.getInfo());
    }
}
