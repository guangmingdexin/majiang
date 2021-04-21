package com.guang.majiangclient.client.layout.show;

import com.guang.majiang.layout.Layout;
import com.guang.majiangclient.client.event.RandomMatchEvent;
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
            new Layout().start(new Stage());
        });

        Menu onlineGame = new Menu("联机");
        MenuItem randomMatch = new MenuItem("随机匹配");
        MenuItem friendMatch = new MenuItem("好友匹配");
        randomMatch.setOnAction(new RandomMatchEvent(7000, "127.0.0.1"));
        onlineGame.getItems().addAll(randomMatch, friendMatch);

        Menu setUpMenu = new Menu("设置");

        menuBar.getMenus().addAll(aloneGameMenu, onlineGame, setUpMenu);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
