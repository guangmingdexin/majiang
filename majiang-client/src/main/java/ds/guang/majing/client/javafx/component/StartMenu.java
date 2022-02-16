package ds.guang.majing.client.javafx.component;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * 开始菜单
 *
 * @author guangmingdexin
 * @date 2021/3/21 14:53
 *
 **/
public class StartMenu extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     *  跳转时带入的数据
     */
    public Map<String, Object> params = new HashMap<>(8);

    public StartMenu() {
        this.params = new HashMap<>(8);
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

        onlineGame.getItems().addAll(randomMatch, friendMatch);

        Menu friendMenu = new Menu();
        Label friendMenuItem = new Label("好友列表");

        randomMatch.setOnAction(event -> {

            // 随机匹配

        });

        friendMenu.setGraphic(friendMenuItem);
        friendMenuItem.setOnMouseClicked(event -> {
            // 启动 friendList
            System.out.println("获取好友列表！");

        });

        Menu setUpMenu = new Menu("设置");

        menuBar.getMenus().addAll(aloneGameMenu, onlineGame, friendMenu, setUpMenu);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
