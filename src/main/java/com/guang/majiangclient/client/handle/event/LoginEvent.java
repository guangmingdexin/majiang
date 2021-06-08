package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.layout.LoginLayout;
import com.guang.majiangclient.client.layout.StartMenu;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * @ClassName LoginEvent
 * @Author guangmingdexin
 * @Date 2021/4/24 21:28
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
@AllArgsConstructor
public class LoginEvent implements Runnable {

    private String msg;

    private boolean res;

    private User user;

    @SneakyThrows
    @Override
    public void run() {
        if(res) {
            // 跳转到菜单
            // 将用户信息缓存到 用户缓存中
            CacheUtil.addUserInfo(user);
            LoginLayout.stage.close();
            StartMenu startMenu = new StartMenu();
            startMenu.start(new Stage());
        }else {
            // 报错
            System.out.println(msg);
        }
    }
}
