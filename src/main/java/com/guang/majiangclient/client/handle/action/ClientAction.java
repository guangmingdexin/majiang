package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.message.AuthResponseMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
public interface ClientAction {

    /**
     * @param message 从服务端接收到的统一消息格式
     */
    void excute(ChannelHandlerContext ctx, AuthResponseMessage message);
}
