package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.entity.PlayGameInfo;
import com.guang.majiangclient.client.handle.event.GameInfoUIEvent;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import com.guang.majiangclient.client.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * @ClassName GameInfoAction
 * @Author guangmingdexin
 * @Date 2021/6/2 8:47
 * @Version 1.0
 **/
@Action(event = Event.GAMEINFO)
public class GameInfoAction implements ClientAction {

    @Override
    public void excute(ChannelHandlerContext ctx, AuthResponseMessage message) {
        AuthResponse response = message.getResponse();
        Service center = ConfigOperation.getCenter();
        PlayGameInfo o = (PlayGameInfo) JsonUtil.mapToObj((Map<String, Object>) response.getBody(), PlayGameInfo.class);
        center.submit(new GameInfoUIEvent(o, response.isResult()));
    }
}
