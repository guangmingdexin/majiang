package com.guang.majiangclient.client.handle.codec;

import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.handle.action.ActionFactory;
import com.guang.majiangclient.client.handle.action.ClientAction;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.PingRequestMessage;
import com.guang.majiangclient.client.util.CommonUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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

        AuthResponseMessage message = (AuthResponseMessage) o;
        AuthResponse response = message.getResponse();
        ClientAction action = ActionFactory.action(response.getEvent());
        action.excute(ctx, message);
    }

    // 断线重连
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                ctx.channel().writeAndFlush(new PingRequestMessage("heat beat"));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
