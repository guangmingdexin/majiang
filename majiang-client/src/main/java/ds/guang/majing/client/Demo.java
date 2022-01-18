package ds.guang.majing.client;


import ds.guang.majing.client.rule.platform.PlatFormRuleImpl;
import ds.guang.majing.common.util.DsConstant;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.dto.User;
import ds.guang.majing.common.game.rule.Rule;
import ds.guang.majing.common.state.StateMachine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ds.guang.majing.common.util.DsConstant.EVENT_POST_TAKE_CARD_ID;
import static ds.guang.majing.common.util.DsConstant.STATE_TAKE_CARD_ID;

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
        Button button = new Button("登录");
        Button take = new Button("发牌");

        Executor executor = new ExtendedExecutor(
                10,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024),
                new ThreadFactory() {

                    AtomicInteger index = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "test-thread-" + index.incrementAndGet());
                    }
                }, (r, ex) -> {
                    System.out.println("抛出异常！");
                });

        /**
         * 1.点击按钮
         * 2.发送请求
         * 3.回调
         */
        Button game = new Button("开始游戏！");

        Rule<String, StateMachine<String, String, DsResult>> rule = new PlatFormRuleImpl();

        rule.create("V3-PLATFORM");

        StateMachine<String, String, DsResult> ruleActor = rule.getRuleActor();

        // 1.状态同步
        // 生成一个 requestNo 作为测试
        String requestNo = UUID.randomUUID().toString().substring(0, 8);

        button.setOnAction(event -> {
            // TODO 多次点击登录 会出现返回值不一致的情况
            // TODO 异步状态下，在无法获取服务端的响应下就进行了状态的转换
            // TODO 巨大隐患，当服务器没有即使响应之后
            // TODO 同样还是服务器-客户端状态如何保持一致（在客户端发送正式请求前，先
            //  发送一个预请求，用来同步服务器-客户端状态，或者在处理请求时进行判断，主要是
            //  两者状态不一致应该以谁为准----那还用说----服务器）
            // 原因：点击事件之后，服务器的状态机 和 客户端的状态机 状态不一致
            // 我应该维护一个状态机 map，根据用户 id 或者 其他标识符 作为 key
            // 并需要时刻同步两个状态机的状态一致，缓存
            System.out.println("登陆开始了！");
            User u = new User("guangmingdexin", "123");
            DsMessage data = DsMessage.build(DsConstant.EVENT_LOGIN_ID, requestNo, u);
            CompletableFuture.runAsync(() -> {
                ruleActor.setCurrentState(DsConstant.STATE_LOGIN_ID, data);
                ruleActor.event(DsConstant.EVENT_LOGIN_ID, data);
            });
        });

        game.setOnAction(event -> {
            GameUser gameUser = (GameUser) Cache.getInstance().getObject("guangmingdexin");
            String userId = gameUser == null ? "123456" : gameUser.getUserId();
            DsMessage data = DsMessage.build(DsConstant.EVENT_PREPARE_ID, requestNo, userId);
            CompletableFuture.runAsync(() -> {
                ruleActor.setCurrentState(DsConstant.STATE_PLATFORM_ID, data);
                ruleActor.event(DsConstant.EVENT_PREPARE_ID, data);
            }).exceptionally(e -> {
                System.out.println(e.getMessage());
                return null;
            });
        });

        take.setOnAction(event -> {

            GameUser gameUser = (GameUser) Cache.getInstance().getObject("guangmingdexin");
            String userId = gameUser == null ? "123456" : gameUser.getUserId();
            DsMessage data = DsMessage.build(EVENT_POST_TAKE_CARD_ID, requestNo, userId);
            CompletableFuture.runAsync(() -> {
                ruleActor.setCurrentState(STATE_TAKE_CARD_ID, data);
                ruleActor.event(EVENT_POST_TAKE_CARD_ID, data);
            }).exceptionally(e -> {
                System.out.println(e.getMessage());
                return null;
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
