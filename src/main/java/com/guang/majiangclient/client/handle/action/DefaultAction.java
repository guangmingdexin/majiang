package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangserver.handle.action.ServerAction;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName DefaultAction
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/16 15:27
 * @Version 1.0
 **/
public class DefaultAction implements ClientAction {
    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        System.out.println("默认业务处理类！");
    }
}
