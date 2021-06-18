package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.entity.Room;
import com.guang.majiangclient.client.handle.event.RandomMatchUIEvent;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.util.ConfigOperation;
import com.guang.majiangclient.client.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * @ClassName MatchAction
 * @Author guangmingdexin
 * @Date 2021/5/19 14:37
 * @Version 1.0
 **/
@Action(event = Event.RANDOMGAME)
public class MatchAction implements ClientAction {

    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        AuthResponse response = message.getResponse();
        System.out.println("收到的信息！" + response.getMsg());
        Service center = ConfigOperation.getCenter();
        center.submit(new RandomMatchUIEvent(response));
    }
}
