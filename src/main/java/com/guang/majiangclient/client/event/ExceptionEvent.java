package com.guang.majiangclient.client.event;

import com.guang.majiangclient.client.common.Event;
import javafx.scene.control.Alert;

/**
 * @ClassName ExceptionEvent
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/19 11:00
 * @Version 1.0
 **/
public class ExceptionEvent implements Runnable {

    private Event event;

    private String msg;

    public ExceptionEvent(Event event, String msg) {
        this.event = event;
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
