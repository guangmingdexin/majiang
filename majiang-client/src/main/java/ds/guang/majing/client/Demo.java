package ds.guang.majing.client;


import ds.guang.majing.client.action.Action;
import ds.guang.majing.client.action.LoginAction;
import ds.guang.majing.client.event.Event;
import ds.guang.majing.client.event.LoginEvent;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.dto.User;
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

        /**
         * 1.点击按钮
         * 2.发送请求
         * 3.回调
         */
        Button game = new Button("开始游戏！");

        DsResult result = DsResult.ok();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        button.setOnAction(event -> {
            System.out.println("登陆开始了！");
            User u = new User("guangmingdexin", "123");
            // 1.向事件中心发送消息，触发方法
            // 2.消息处理之后能够处理回调
            // 这段代码就是业务代码，如何封装为一个 event 由 如何由 action 执行

            // 返回值如何解决
            // 这样的话，不如直接到 Runnable 接口
            // 而且如何解决 同步异步问题
            // 比如另一个线程 完成了任务如何通知 javafx 线程让他进行跳转
            // 又需要大量处理完全不需要，直接同步处理

            // 1.经过测试必定需要异步框架处理，不然性能太差
            // 2.event 封装为一个 runnable
            // 3.仿照类似于 Flux 的订阅，消费者模式 调用远程服务即可以封装为 数据源
                // 而具体的业务代码即可以看作消费者
           CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return new LoginEvent(u).call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return DsResult.error();
                    }).thenApply(dsResult -> {
                           if(result.isOk()) {
                               // 进入游戏
                               System.out.println("thread-name: " + Thread.currentThread().getName() + " success 进入游戏!");
                           }else {
                               // 跳出弹框
                           }
                           return dsResult;
                     }).exceptionally(ex -> {
                       System.out.println("发生异常"+ex.getMessage());
                       return null;
                     });

            // 注册一个回调函数
            // 当执行函数做完之后，自动执行回调函数，再由 JavaFx 线程执行此函数
            // 需要传入另一个线程执行传入的结果，同时还需要保留 线程的引用
        });


        grid.add(button, 0, 0, 2, 1);
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        primaryStage.setTitle("登陆");
        primaryStage.show();
    }
}
