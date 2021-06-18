package com.guang.majiangclient.client.layout;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.Friend;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.message.FriendRequestMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * 好友排行榜
 *
 * @ClassName FriendList
 * @Author guangmingdexin
 * @Date 2021/6/16 16:41
 * @Version 1.0
 **/

public class FriendList extends Application {

   public static Stage stage;

   public static VBox vBox;

   public static VBox vBoxFriend;

   private Friend friend;


   static {
       stage = new Stage();
       vBox = new VBox();
       vBoxFriend = new VBox();
   }

    public FriendList(Friend friend) {
        this.friend = friend;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        HBox menu = new HBox();
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(20d);
        TextField userTextField = new TextField();
        Button add = new Button("添加好友");

        add.setOnMouseClicked(event -> {
            // 获取输入的电话
            String tel = userTextField.getText();
            Service center = ConfigOperation.getCenter();
            center.submit(
                    new Task<>(Event.FRIEND, new Friend(CacheUtil.getUserInfo().getUserId(), tel, "add")),
                    FriendRequestMessage.class, Event.FRIEND
            );
        });

        menu.getChildren().addAll(userTextField, add);

        vBox.getChildren().add(menu);
        vBox.setSpacing(50d);
        vBox.getChildren().add(vBoxFriend);

        List<User> friends = friend.getFriends();
        if(friends != null) {
            friends.forEach(
                    user -> {
                        HBox hBox = new HBox();
                        Label name = new Label(user.getUserName());
                        Label score = new Label(String.valueOf(user.getScore()));
                        hBox.getChildren().addAll(name, score);
                        hBox.setAlignment(Pos.CENTER);
                        hBox.setSpacing(20d);
                        vBoxFriend.getChildren().add(hBox);
                    });
        }

        Scene scene = new Scene(vBox,300, 275);
        stage.setScene(scene);

        stage.setTitle("麻将小游戏");
        stage.show();
    }
}
