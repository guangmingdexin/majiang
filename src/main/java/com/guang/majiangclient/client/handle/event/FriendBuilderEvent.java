package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.entity.Friend;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.layout.FriendList;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangclient.client.util.JsonUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

/**
 * @ClassName FriendBuilderEvent
 * @Author guangmingdexin
 * @Date 2021/6/17 14:11
 * @Version 1.0
 **/
@AllArgsConstructor
@RunnableEvent(value = Event.UIEVENT)
public class FriendBuilderEvent implements Runnable {

    private AuthResponse response;


    @SneakyThrows
    @Override
    public void run() {


        Friend f = (Friend) JsonUtil.mapToObj((Map<String, Object>) response.getBody(), Friend.class);
        if("get".equals(f.getType())) {
            // 1. 判断 Redis 中是否有缓存
            FriendList friendList = new FriendList(f);
            friendList.start(null);
        }else if("add".equals(f.getType())) {
            FriendList.vBoxFriend.getChildren().clear();
            List<User> friends = f.getFriends();
            if(friends != null) {
                friends.forEach(
                        user -> {
                            HBox hBox = new HBox();
                            Label name = new Label(user.getUserName());
                            Label score = new Label(String.valueOf(user.getScore()));
                            hBox.getChildren().addAll(name, score);
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setSpacing(20d);
                            FriendList.vBoxFriend.getChildren().add(hBox);
                        });
            }
        }
    }
}
