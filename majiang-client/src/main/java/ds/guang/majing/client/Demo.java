package ds.guang.majing.client;


import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.remote.dto.ao.AccountAo;
import ds.guang.majing.client.remote.dto.ao.UserQueryAo;
import ds.guang.majing.client.remote.service.IUserService;
import ds.guang.majing.client.remote.service.UserService;
import ds.guang.majing.client.rule.PlatFormRuleImpl;
import ds.guang.majing.common.game.card.CardType;
import ds.guang.majing.common.game.card.MaJiang;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.client.cache.Cache;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.rule.Rule;
import ds.guang.majing.common.state.StateMachine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2021-12-08 15:51
 */
public class Demo extends Application {


    public static void main(String[] args) {
        launch(args);
    }


    public static Button pong;

    public static Button gang;

    public static Button hu;

    public static Button ignore;

    public static StateMachine<String, String, DsResult> ruleActor;

    @Override
    public void start(Stage primaryStage) throws Exception  {
        // 1.创建一个按钮
        GridPane grid = new GridPane();
        Button button = new Button("登录");
        Button takeOut = new Button("出牌");

        pong = new Button("碰");
        gang = new Button("杠");
        hu = new Button("胡");
        ignore = new Button("过");


        pong.setVisible(false);
        gang.setVisible(false);
        hu.setVisible(false);
        ignore.setVisible(false);

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

            // TODO 巨大隐患，当服务器没有即使响应之后
            // TODO 同样还是服务器-客户端状态如何保持一致（在客户端发送正式请求前，先
            //  发送一个预请求，用来同步服务器-客户端状态，或者在处理请求时进行判断，主要是
            //  两者状态不一致应该以谁为准----那还用说----服务器）
            // 原因：点击事件之后，服务器的状态机 和 客户端的状态机 状态不一致
            // 我应该维护一个状态机 map，根据用户 id 或者 其他标识符 作为 key
            // 并需要时刻同步两个状态机的状态一致，缓存
            System.out.println("登陆开始了！");

            AccountAo accountAo = new AccountAo("test3", "123");

            CompletableFuture.runAsync(() -> {
                IUserService userService = new UserService();
                userService.login(accountAo);

                userService.getFriends(new UserQueryAo().setUserId(CacheUtil.getUserId()));
            });

        });

        game.setOnAction(event -> {
             // 开始游戏
            // 1.加入游戏池，将状态机状态设置为进入游戏状态
            // 2.匹配成功，则获取到房间信息
            CompletableFuture.runAsync(() -> {

                System.out.println("开始匹配！........");

                ruleActor.setCurrentState(STATE_PREPARE_ID, null);

                ruleActor.event(EVENT_RANDOM_MATCH_ID, null);
            }).exceptionally(e -> {

                e.printStackTrace();

                return null;
            });
        });

        takeOut.setOnAction(event -> {

            //System.out.println("........................");
            //System.out.println("出牌！");

            GameUser gameUser = (GameUser) Cache.getInstance().getObject("guangmingdexin");
            String userId = gameUser == null ? "123456" : gameUser.getUserId();

            // 1.获取手牌
            Room room = (Room) Cache.getInstance().getObject(preRoomInfoPrev(userId));

            if(room.isCurAround(userId)) {

                Player p = room.findPlayerById(userId);
                int r = new Random().nextInt(p.getCards().size());

                Integer value = p.getCards().get(r);
                DsMessage data = DsMessage.build(EVENT_TAKE_OUT_CARD_ID,
                        requestNo,
                        new GameInfoRequest()
                                .setUserId(userId)
                                .setCard(new MaJiang(value, CardType.generate(value))));

                CompletableFuture.runAsync(() -> {
                    System.out.println("出牌 " + value);
                    ruleActor.event(EVENT_TAKE_OUT_CARD_ID, data);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });

            }else {

                System.out.println("不是你的回合");
            }

        });

        grid.add(button, 0, 0, 2, 1);
        grid.add(game, 0, 1, 2, 1);
        grid.add(takeOut, 0, 2, 2, 1);

        grid.add(pong, 0, 3);
        grid.add(gang, 1, 3);
        grid.add(hu, 2, 3);
        grid.add(ignore, 3, 3);

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        primaryStage.setTitle("登陆");
        primaryStage.show();
    }
}
