package com.guang.majiangclient.client.event;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;


/**
 * @ClassName RandomMatchEvent
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/23 8:56
 * @Version 1.0
 **/
public class RandomMatchEvent implements EventHandler<ActionEvent> {

    private int port;

    private String host;

    public RandomMatchEvent(int port, String host) {
        this.port = port;
        this.host = host;
    }

    @Override
    public void handle(ActionEvent event) {

        // 1. 从 服务器 获取玩家基本信息以及头像

        // 加载背景 头像到合适位置
        // 同时获取 另外几个玩家的信息 加载到本地

    }
}
