package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.message.AuthRequestMessage;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

/**
 * @ClassName AuthAction
 * @Description 从数据库获取用户信息 返回给客户端
 * @Author guangmingdexin
 * @Date 2021/4/8 14:34
 * @Version 1.0
 **/
@Action
public class AuthAction implements ServerAction<AuthRequestMessage, AuthResponseMessage> {

    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, AuthRequestMessage request, AuthResponseMessage response) {
        System.out.println("执行业务逻辑！");


        // TODO write 无法接收到消息，writeAndFlush 可以
        // ctx.writeAndFlush();
    }
}
