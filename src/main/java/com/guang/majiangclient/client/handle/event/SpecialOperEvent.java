package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.GameInfoRequest;
import com.guang.majiangclient.client.entity.PlayGameInfo;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import lombok.AllArgsConstructor;

/**
 * 特殊事件渲染任务，点击进行下一流程
 *
 * @ClassName SpecialBuildEevent
 * @Author guangmingdexin
 * @Date 2021/6/7 15:10
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
@AllArgsConstructor
public class SpecialOperEvent implements Runnable{

    private PlayGameInfo info;

    @Override
    public void run() {
        // 加载特殊事件图片
        Service center = ConfigOperation.getCenter();

        int res = info.getRes();
        System.out.println("SpecialOperEvent-res: " + res);

        ClientLayout.pong.setVisible(true);
        ClientLayout.gang.setVisible(true);
        ClientLayout.hu.setVisible(true);
        ClientLayout.ignore.setVisible(true);
        if((res & GameEvent.Pong.intValue()) == GameEvent.Pong.intValue()) {
            ClientLayout.pong.setOpacity(1);
        }else {
            ClientLayout.pong.setOpacity(0.5);
        }

        if(((res & GameEvent.Gang1.intValue()) == GameEvent.Gang1.intValue())
        || ((res & GameEvent.Gang2.intValue()) == GameEvent.Gang2.intValue())
                || ((res & GameEvent.Gang3.intValue()) == GameEvent.Gang3.intValue())) {
            ClientLayout.gang.setOpacity(1);
        }else {
            ClientLayout.gang.setOpacity(0.5);
        }

        if((res & GameEvent.Hu.intValue()) == GameEvent.Hu.intValue()) {
            ClientLayout.hu.setOpacity(1);
        }else {
            ClientLayout.hu.setOpacity(0.5);
        }

        ClientLayout.pong.setOnMouseClicked(event -> {
            // 发送消息
            // 等待回复
            System.out.println("pong!");
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), CacheUtil.getGameUserInfo().getUserId(), info.getValue(),
                                            CacheUtil.getGameUserInfo().getDirection(), GameEvent.AckEvent, info.getRes(), GameEvent.Pong.intValue(), true)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });

        ClientLayout.gang.setOnMouseClicked(event -> {
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), CacheUtil.getGameUserInfo().getUserId(), info.getValue(),
                                            CacheUtil.getGameUserInfo().getDirection(), GameEvent.AckEvent, info.getRes(), GameEvent.Gang2.intValue(), true)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });

        ClientLayout.hu.setOnMouseClicked(event -> {
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), CacheUtil.getGameUserInfo().getUserId(), info.getValue(),
                                            CacheUtil.getGameUserInfo().getDirection(), GameEvent.AckEvent, info.getRes(), GameEvent.Hu.intValue(), true)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });

        ClientLayout.ignore.setOnMouseClicked(event -> {
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(info.getRoomId(), CacheUtil.getGameUserInfo().getUserId(), info.getValue(),
                                            CacheUtil.getGameUserInfo().getDirection(), GameEvent.AckEvent, info.getRes(), GameEvent.Ignore.intValue(), false)
                            )), RandomMatchRequestMessage.class, Event.RANDOMGAME

            );
        });
    }


}
