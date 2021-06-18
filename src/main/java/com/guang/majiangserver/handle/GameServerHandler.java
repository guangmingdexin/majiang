package com.guang.majiangserver.handle;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangserver.handle.action.ActionFactory;
import com.guang.majiangserver.handle.action.RandomMatchAction;
import com.guang.majiangserver.handle.action.ServerAction;
import com.guang.majiangserver.util.ResponseUtil;
import com.guang.majiangserver.util.ServerCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @ClassName GameServerHandler
 * @Description
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
        // 获取业务处理类
        ActionFactory.ActionBean action = ActionFactory.action(o.getClass());
        ServerAction serverAction = action.getAction();
        serverAction.execute(ctx, group, o, new AuthResponseMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            System.out.println(((IdleStateEvent) evt).state());
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                System.out.println("心跳检查超时！");
                close(ctx);
                // 在规定时间内没有收到客户端的心跳数据, 主动断开连接
                // TODO 复杂的业务故障处理
                // 1.首先判断玩家的游戏状态
                // 1.1 玩家可能正准备匹配（玩家信息保存在匹配池中）
                // 1.2 玩家已经匹配成功
                // 首先根据 channel 获取玩家信息
            }
        } else if(evt instanceof ChannelInputShutdownReadComplete) {
            System.out.println("客户端关闭");
            close(ctx);
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void close(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String channelId = ServerCache.channelId(channel);
        Long userId = ServerCache.getUserId(channelId);
        RandomMatchAction.removeMatchPool(userId);
        group.remove(channel);
        ServerCache.removeUserId(channelId);
        ctx.disconnect();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
         // TODO 将异常日志写入日志文件
         cause.printStackTrace();
        System.out.println("服务器发生异常！");
        AuthResponseMessage response = new AuthResponseMessage();
        ResponseUtil.responseBuildFactory(response, null, 500, Event.UIEVENT, "服务其出现异常", false);
        ctx.writeAndFlush(response);
    }
}
