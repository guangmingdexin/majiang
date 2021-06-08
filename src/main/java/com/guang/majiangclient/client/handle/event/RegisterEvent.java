package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import javafx.scene.control.Alert;
import lombok.AllArgsConstructor;

/**
 * @ClassName RegisterSuccessEvent
 * @Author guangmingdexin
 * @Date 2021/4/19 10:49
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
@AllArgsConstructor
public class RegisterEvent implements Runnable {

    private String msg;

    @Override
    public void run() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("注册");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
