package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.GameInfoRequest;
import com.guang.majiangclient.client.entity.PlayGameInfo;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import com.guang.majiangclient.client.util.ImageUtil;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.AllArgsConstructor;

/**
 * 特殊事件渲染任务，点击进行下一流程
 *
 * @ClassName SpecialBuildEevent
 * @Author guangmingdexin
 * @Date 2021/6/7 15:10
 * @Version 1.0
 **/
@Action(event = Event.UIEVENT)
@AllArgsConstructor
public class SpecialOperEvent implements Runnable{

    private PlayGameInfo info;

    @Override
    public void run() {
        // 加载特殊事件图片
        Service center = ConfigOperation.getCenter();

        int res = info.getRes();

        ClientLayout.pong.setVisible(true);
        ClientLayout.gang.setVisible(true);
        ClientLayout.hu.setVisible(true);
        ClientLayout.ignore.setVisible(true);

        if((res & GameEvent.Pong.intValue()) == 1) {
            ClientLayout.pong.setOpacity(1);
        }else {
            ClientLayout.pong.setOpacity(0.5);
        }

        if((res & GameEvent.Gang.intValue()) == 1) {
            ClientLayout.gang.setOpacity(1);
        }else {
            ClientLayout.gang.setOpacity(0.5);
        }

        if((res & GameEvent.Hu.intValue()) == 1) {
            ClientLayout.hu.setOpacity(1);
        }else {
            ClientLayout.hu.setOpacity(0.5);
        }

        ClientLayout.pong.setOnMouseClicked(event -> {
            // 发送消息
            // 等待回复
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), info.getUserId(), info.getValue(),
                                            info.getDirection(), GameEvent.Ack, info.getRes(), GameEvent.Pong.intValue(), true)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });

        ClientLayout.gang.setOnMouseClicked(event -> {
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), info.getUserId(), info.getValue(),
                                            info.getDirection(), GameEvent.Ack, info.getRes(), GameEvent.Gang.intValue(), true)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });

        ClientLayout.hu.setOnMouseClicked(event -> {
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), info.getUserId(), info.getValue(),
                                            info.getDirection(), GameEvent.Ack, info.getRes(), GameEvent.Hu.intValue(), true)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });

        ClientLayout.ignore.setOnMouseClicked(event -> {
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), info.getUserId(), info.getValue(),
                                            info.getDirection(), GameEvent.Ack, info.getRes(), GameEvent.Ignore.intValue(), true)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });
    }


}
