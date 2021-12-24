package ds.guang.majing.client;


import ds.guang.majing.client.rule.platform.PlatFormRuleImpl;
import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.cache.DsGlobalCache;
import ds.guang.majing.common.cache.DsGlobalCacheDefaultImpl;
import ds.guang.majing.common.dto.User;
import ds.guang.majing.common.rule.Rule;
import ds.guang.majing.common.state.StateMachine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.concurrent.*;

/**
 * @author guangyong.deng
 * @date 2021-12-08 15:51
 */
public class Demo extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception  {
        // 1.创建一个按钮
        GridPane grid = new GridPane();
        Button button = new Button("demo");

        DsGlobalCache cache = new DsGlobalCacheDefaultImpl();
        /**
         * 1.点击按钮
         * 2.发送请求
         * 3.回调
         */
        Button game = new Button("开始游戏！");

        Rule<String, StateMachine<String, String, DsResult>> rule = new PlatFormRuleImpl();

        rule.create("V3-PLATFORM");

        StateMachine<String, String, DsResult> ruleActor = rule.getRuleActor();

        System.out.println("ruleActor: " + ruleActor);

        button.setOnAction(event -> {
            // TODO 多次点击登录 会出现返回值不一致的情况
            // TODO 异步状态下，在无法获取服务端的响应下就进行了状态的转换
            // TODO 巨大隐患，当服务器没有即使响应之后
            // 原因：点击事件之后，服务器的状态机 和 客户端的状态机 状态不一致
            // 我应该维护一个状态机 map，根据用户 id 或者 其他标识符 作为 key
            // 并需要时刻同步两个状态机的状态一致，缓存
            System.out.println("登陆开始了！");
            User u = new User("guangmingdexin", "123");

            CompletableFuture.runAsync(() -> {
                ruleActor.setCurrentState(DsConstant.STATE_LOGIN_ID, u);
                ruleActor.event(DsConstant.EVENT_LOGIN_ID, u);

            });
        });

        game.setOnAction(event -> {

            Object data = DsGlobalCache.getInstance().getObject("guangmingdexin");
            System.out.println("准备匹配玩家，构造房间！" + data);

            CompletableFuture.runAsync(() -> {
                ruleActor.setCurrentState(DsConstant.STATE_PLATFORM_ID, data);
                ruleActor.event(DsConstant.EVENT_PREPARE_ID, data);
            });

        });


        grid.add(button, 0, 0, 2, 1);
        grid.add(game, 0, 1, 2, 1);
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        primaryStage.setTitle("登陆");
        primaryStage.show();
    }
}
