package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.entity.PlayGameInfo;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.util.JsonUtil;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @ClassName SpecialBuildEvent
 * @Author guangmingdexin
 * @Date 2021/6/7 17:23
 * @Version 1.0
 **/
@Action(event = Event.UIEVENT)
@AllArgsConstructor
public class SpecialBuildEvent implements Runnable {

    private AuthResponse response;

    @Override
    public void run() {
        // 点击事件无论成功还是失败，首先需要将按钮界面隐藏
        ClientLayout.pong.setVisible(false);
        ClientLayout.gang.setVisible(false);
        ClientLayout.hu.setVisible(false);
        ClientLayout.ignore.setVisible(false);

        if(response.isResult()) {
            PlayGameInfo o = (PlayGameInfo) JsonUtil.mapToObj((Map<String, Object>) response.getBody(), PlayGameInfo.class);
            
        }
    }
}
