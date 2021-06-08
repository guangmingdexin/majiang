package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import javafx.scene.control.Alert;
import lombok.NoArgsConstructor;

/**
 * @ClassName ServerLinkedError
 * @Description
 * @Author guangmingdexin
 * @Date 2021/3/30 10:22
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
@NoArgsConstructor
public class ServerLinkedError implements Runnable {

    @Override
    public void run() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setContentText("连接服务器错误！");

        alert.showAndWait();
    }
}
