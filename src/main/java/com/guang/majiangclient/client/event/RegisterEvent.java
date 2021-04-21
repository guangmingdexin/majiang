package com.guang.majiangclient.client.event;

import com.guang.majiangclient.client.common.Event;
import javafx.scene.control.Alert;

/**
 * @ClassName RegisterSuccessEvent
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/19 10:49
 * @Version 1.0
 **/
public class RegisterEvent implements Runnable {

    private Event event;

    private String msg;

    public RegisterEvent(Event event, String msg) {
        this.event = event;
        this.msg = msg;
    }

    @Override
    public void run() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("注册");
        alert.setContentText(msg);

        alert.showAndWait();
    }
}
