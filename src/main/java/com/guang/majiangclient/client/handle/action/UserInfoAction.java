package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName UserInfoAction
 * @Description 将 用户信息加入到暂时缓存中，
 * @Author guangmingdexin
 * @Date 2021/4/16 15:29
 * @Version 1.0
 **/
@Action
public class UserInfoAction implements ClientAction {

    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        // 首先获取数据
        Object body = message.getResponse().getBody();
        // 判断 body 类型
        if(body instanceof User) {
            // 强转
        }else {
            throw new IllegalArgumentException("非法的类型！");
        }
    }
}
