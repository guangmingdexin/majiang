package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.handle.event.LoginEvent;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.util.ConfigOperation;
import com.guang.majiangclient.client.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * @ClassName LoginAction
 * @Author guangmingdexin
 * @Date 2021/4/24 21:24
 * @Version 1.0
 **/
@Action(event = Event.LOGIN)
public class LoginAction implements ClientAction {

    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        System.out.println("执行客户端登陆！");
        AuthResponse response = message.getResponse();
        Service center = ConfigOperation.getCenter();
        center.submit(new LoginEvent(response.getMsg(), response.isResult(),
                (User) JsonUtil.mapToObj((Map<String, Object>) response.getBody(), User.class)));
    }
}
