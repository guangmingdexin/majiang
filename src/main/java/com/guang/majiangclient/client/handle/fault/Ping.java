package com.guang.majiangclient.client.handle.fault;

import com.guang.majiangclient.client.message.PingRequestMessage;
import com.guang.majiangclient.client.util.CommonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName Ping
 * @Author guangmingdexin
 * @Date 2021/6/16 10:02
 * @Version 1.0
 **/
public class Ping extends ChannelInboundHandlerAdapter {

    /**
     * 客户端连接到服务器端后，等待WAIT_TIME秒，然后ping一下 Server端，即发送一个心跳包
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        ping(channel);
    }

    private void ping(Channel channel) {
        ScheduledFuture<?> schedule = channel.eventLoop().schedule(() -> {
            if (channel.isActive()) {
                System.out.println("发送 heat beat");
                channel.writeAndFlush(new PingRequestMessage("heat beat"));
            } else {
                System.err.println("The connection had broken, cancel the task that will send a heart beat.");
                channel.closeFuture();
                throw new RuntimeException();
            }
        }, CommonUtil.WRITE_TIME_OUT, TimeUnit.SECONDS);

        schedule.addListener(future -> {
            if(future.isSuccess()) {
                ping(channel);
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当Channel已经断开的情况下, 仍然发送数据, 会抛异常, 该方法会被调用.
        cause.printStackTrace();
        ctx.close();
    }
}
