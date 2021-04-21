package com.guang.majiangclient.client.event;

import com.guang.majiangclient.client.common.Event;
import javafx.scene.control.Alert;

/**
 * @ClassName ServerLinkedError
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/30 10:22
 * @Version 1.0
 **/
public class ServerLinkedError implements Runnable {

    private Event event;

    public ServerLinkedError(Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setContentText("连接服务器错误！");

        alert.showAndWait();
    }
}
