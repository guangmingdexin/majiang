package com.guang.majiangclient.client.layout;

import com.guang.majiang.layout.Layout;
import com.guang.majiangclient.client.GameClient;
import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.Friend;
import com.guang.majiangclient.client.entity.GameInfoRequest;
import com.guang.majiangclient.client.entity.GameUser;
import com.guang.majiangclient.client.entity.PlayGameInfo;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.message.FriendRequestMessage;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @ClassName BeginMenu
 * @Description 开始菜单
 * @Author guangmingdexin
 * @Date 2021/3/21 14:53
 * @Version 1.0
 **/
public class StartMenu extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 300, 250, Color.WHITE);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        // File menu - new, save, exit
        Label aloneGame = new Label("单机麻将游戏");
        Menu aloneGameMenu = new Menu();
        aloneGameMenu.setGraphic(aloneGame);

        aloneGame.setOnMouseClicked(event -> {
          //  new Layout().start(new Stage());
        });

        Menu onlineGame = new Menu("联机");
        MenuItem randomMatch = new MenuItem("随机匹配");
        MenuItem friendMatch = new MenuItem("好友匹配");
        Service center = ConfigOperation.getCenter();
        randomMatch.setOnAction(e -> {
            ClientLayout layout = new ClientLayout();
            try {
                layout.start(null);
                center.submit(
                        new Task<>(Event.RANDOMGAME,
                                new GameInfoRequest(
                                        new GameUser(
                                                CacheUtil.getUserInfo().getUserId(),
                                                CacheUtil.getUserInfo().getUserName(),
                                                null,
                                                System.currentTimeMillis(),
                                                -1,
                                                GameClient.getChannel().id().toString()),
                                        new PlayGameInfo(GameEvent.InitialGame))),
                        RandomMatchRequestMessage.class,
                        Event.RANDOMGAME);
              //  primaryStage.close();
            } catch (Exception ex) {
                System.out.println("匹配失败！");
                ex.printStackTrace();
            }
        });

        onlineGame.getItems().addAll(randomMatch, friendMatch);

        Menu friendMenu = new Menu();
        Label friendMenuItem = new Label("好友列表");

        friendMenu.setGraphic(friendMenuItem);
        friendMenuItem.setOnMouseClicked(event -> {
            // 启动 friendList
            System.out.println("获取好友列表！");

            center.submit(
                    new Task<>(
                        Event.FRIEND,
                        new Friend(CacheUtil.getUserInfo().getUserId(), "get")),
                    FriendRequestMessage.class,
                    Event.FRIEND);
        });

        Menu setUpMenu = new Menu("设置");

        menuBar.getMenus().addAll(aloneGameMenu, onlineGame, friendMenu, setUpMenu);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
