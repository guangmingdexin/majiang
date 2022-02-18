package ds.guang.majiang.server.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *
 * 将字节解码为消息
 *
 * @author guangyong.deng
 * @date 2022-02-17 14:03
 */
public class IdleStateCodec extends SimpleChannelInboundHandler<String> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // 心跳包 值为 Long.MAX_VALUE

        System.out.println("idle: " + msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
