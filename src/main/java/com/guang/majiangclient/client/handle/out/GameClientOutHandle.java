package com.guang.majiangclient.client.handle.out;

import io.netty.channel.*;

import java.net.SocketAddress;

/**
 * @ClassName ClientHandle
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/30 8:39
 * @Version 1.0
 **/
public class GameClientOutHandle extends ChannelOutboundHandlerAdapter {

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        promise.addListener(future -> {
            if(future.isSuccess()) {
//                String macAddress = ConfigOperation.getMacAddress();
//                if(macAddress == null) {
//                    macAddress = UUID.randomUUID().toString();
//                }
//                AuthRequestMessage m = new AuthRequestMessage(new User(macAddress));
                System.out.println("连接远程服务器成功！");
             //   ctx.writeAndFlush(m);
            }
        });
    }


}
