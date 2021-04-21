package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.Avatar;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.message.AuthRequestMessage;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangserver.config.ConfigOperation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

import java.util.Date;

/**
 * @ClassName AuthAction
 * @Description 从数据获取用户信息 返回给客户端
 * @Author guangmingdexin
 * @Date 2021/4/8 14:34
 * @Version 1.0
 **/
@Action
public class AuthAction implements ServerAction<AuthRequestMessage, AuthResponseMessage> {

    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, AuthRequestMessage request, AuthResponseMessage response) {
        System.out.println("执行业务逻辑！");
        User u = request.getUser();
        // 从数据库中读取用户信息 返回给客户端
        User user = new User();
        user.setTel(u.getTel());
        user.setUserId(1);
        user.setUserName("光明的心");
        Avatar avatar = new Avatar();
        // 使用默认头像
        avatar.setPath(ConfigOperation.getDefaultAvatar());
        user.setAvatar(avatar);

        AuthResponse resp = new AuthResponse(new Date(), user, 200, Event.REGISTER);
        resp.isSuccess();
        // ChannelHandlerContext.writeAndFlush 只会从当前 handleContext 开始
        // 而 channel.writeAndFlush 会从 tailhandleContext 开始
        // TODO write 无法接收到消息，writeAndFlush 可以
        ctx.writeAndFlush(new AuthResponseMessage(resp));
    }
}
