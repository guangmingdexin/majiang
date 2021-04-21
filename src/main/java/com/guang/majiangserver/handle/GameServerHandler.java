package com.guang.majiangserver.handle;

import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangserver.handle.action.ActionFactory;
import com.guang.majiangserver.handle.action.ServerAction;
import com.guang.majiangserver.util.ResponseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Date;

/**
 * @ClassName GameServerHandler
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/23 8:34
 * @Version 1.0
 **/
public class GameServerHandler extends SimpleChannelInboundHandler {

    /**
     * 定义一个 channel 组 管理所有的channel
     * GlobalEventExecutor 全局事件处理器
     */
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[客户端]" + channel.remoteAddress() + "加入游戏！");
        // 使用一个全局 channel 管理器
        group.add(channel);

//        AuthResponse resp = new AuthResponse(new Date(), new User("11233"), 200);
//        resp.isSuccess();
//        // ChannelHandlerContext.writeAndFlush 只会从当前 handleContext 开始
//        // 而 channel.writeAndFlush 会从 tailhandleContext 开始
//        ctx.writeAndFlush(new AuthResponseMessage(resp));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        System.out.println("获取信息： " + o);
        // 获取业务处理类
        ActionFactory.ActionBean action = ActionFactory.action(o.getClass());

        ServerAction serverAction = action.getAction();

        serverAction.execute(ctx, group, o, new AuthResponseMessage());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
         // TODO 将异常日志写入日志文件
        // cause.printStackTrace();
        System.out.println("服务器发生异常！");
        AuthResponseMessage response = new AuthResponseMessage();
        ResponseUtil.responseBuildFactory(response, null, 500, Event.EXCEPTION, "服务其出现异常", false);
        ctx.writeAndFlush(response);
    }
}
