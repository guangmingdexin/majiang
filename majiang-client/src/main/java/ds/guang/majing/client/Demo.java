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
        DsResult result = DsResult.ok();
        button.setOnAction(event -> {
            User u = new User("guangmingdexin", "111");
            // 1.向事件中心发送消息，触发方法
            // 2.消息处理之后能够处理回调
            // 这段代码就是业务代码，如何封装为一个 event 由 如何由 action 执行

            // 返回值如何解决
            // 这样的话，不如直接到 Runnable 接口
            // 而且如何解决 同步异步问题
            // 比如另一个线程 完成了任务如何通知 javafx 线程让他进行跳转
            // 又需要大量处理完全不需要，直接同步处理
            Action<Event, DsResult> action = new LoginAction();
            action.action(new LoginEvent(u));
            if(result.isOk()) {
                // 进入游戏
                System.out.println("thread-name: " + Thread.currentThread().getName() + " success 进入游戏!");
            }else {
                // 跳出弹框
            }
        });


        grid.add(button, 0, 0, 2, 1);
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        primaryStage.setTitle("登陆");
        primaryStage.show();
    }
}
