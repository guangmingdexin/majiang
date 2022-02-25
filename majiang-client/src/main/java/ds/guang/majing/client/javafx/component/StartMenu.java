package ds.guang.majing.client.javafx.component;


import ds.guang.majing.client.network.idle.WorkState;
import ds.guang.majing.common.state.StateMachine;
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

import static ds.guang.majing.common.util.DsConstant.EVENT_RANDOM_MATCH_ID;
import static ds.guang.majing.common.util.DsConstant.STATE_PREPARE_ID;

/**
 * 开始菜单
 *
 * @author guangmingdexin
 * @date 2021/3/21 14:53
 *
 **/
public class StartMenu extends Application implements Layout {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     *  跳转时带入的数据
     */
    public Map<String, Object> params;

    public StartMenu() {
        this.params = new HashMap<>(8);
    }

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage;

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

            WorkState worker = (WorkState)get("worker");
            StateMachine actor = (StateMachine)get("actor");

            // 随机匹配
            worker.runAsync(() -> {
                System.out.println("开始匹配！........");
                actor.setCurrentState(STATE_PREPARE_ID, null);
                actor.event(EVENT_RANDOM_MATCH_ID, null);

            });

            synchronized (WorkState.LOCK) {
                // 唤醒工作线程
                WorkState.LOCK.notifyAll();
            }
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

    @Override
    public void set(String name, Object value) {
        params.put(name, value);
    }

    @Override
    public Object get(String name) {
        Object o = params.get(name);
        if(o == null) {
            throw new NullPointerException(name + " is null");
        }
        return o;
    }

    @Override
    public void close() {
        stage.close();
    }
}
