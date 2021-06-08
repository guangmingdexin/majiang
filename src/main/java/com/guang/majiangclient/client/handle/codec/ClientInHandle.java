package com.guang.majiangclient.client.handle.codec;

import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.action.ActionFactory;
import com.guang.majiangclient.client.handle.action.ClientAction;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;

/**
 * @ClassName ClientInHandle
 * @Description 接收服务端发送的信息
 * @Author guangmingdexin
 * @Date 2021/3/30 20:12
 * @Version 1.0
 **/
@AllArgsConstructor
public class ClientInHandle extends SimpleChannelInboundHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        // System.out.println("接收到的消息" + o);
        // System.out.println(ctx.channel());
        AuthResponseMessage message = (AuthResponseMessage) o;
        AuthResponse response = message.getResponse();
        ClientAction action = ActionFactory.action(response.getEvent());

        action.excute(ctx, message);
    }

}
