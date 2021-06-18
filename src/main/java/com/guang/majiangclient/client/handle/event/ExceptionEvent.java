package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Event;
import javafx.scene.control.Alert;

/**
 * @ClassName ExceptionEvent
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/19 11:00
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
public class ExceptionEvent implements Runnable {

    private String msg;

    public ExceptionEvent(String msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setContentText(msg);

        alert.showAndWait();
    }
}
