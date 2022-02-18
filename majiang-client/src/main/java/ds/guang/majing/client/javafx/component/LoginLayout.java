package ds.guang.majing.client.javafx.component;


import ds.guang.majing.client.network.idle.WorkState;
import ds.guang.majing.client.remote.dto.ao.AccountAo;
import ds.guang.majing.client.remote.dto.vo.LoginVo;
import ds.guang.majing.client.remote.service.IUserService;
import ds.guang.majing.client.remote.service.UserService;
import ds.guang.majing.client.rule.PlatFormRuleImpl;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.rule.Rule;
import ds.guang.majing.common.state.StateMachine;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static ds.guang.majing.common.util.DsConstant.EVENT_START_ID;

/**
 *
 *  登录界面
 *
 * @author guangmingdexin
 * @date 2021/4/16 16:05
 **/
public class LoginLayout extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        Text scenetitle = new Text("麻将游戏");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("用户名:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("密码:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button login = new Button("登陆");
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.BOTTOM_CENTER);
        hb.getChildren().add(login);

        Button register = new Button("注册");
        hb.setAlignment(Pos.BOTTOM_RIGHT);
        hb.getChildren().add(register);
        grid.add(hb, 1, 4);


        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);


        Rule<String, StateMachine<String, String, DsResult>> rule = new PlatFormRuleImpl();

        rule.create("V3-PLATFORM");

        StateMachine<String, String, DsResult> ruleActor = rule.getRuleActor();

        WorkState workState = new WorkState("work-state");

        login.setOnAction(e -> {
            System.out.println("登陆！");
            String username = userTextField.getText();
            String pwd = pwBox.getText();

            AccountAo accountAo = new AccountAo(username, pwd);

            workState.runAsync(() -> {

                IUserService userService = new UserService();
                DsResult<LoginVo> rs = userService.login(accountAo);

                if(rs.success()) {
                    ruleActor.event(EVENT_START_ID, null);
                    // 跳转
                    Platform.runLater(() -> {

                        try {

                            StartMenu startMenu = new StartMenu();
                            startMenu.start(new Stage());
                            startMenu.set("actor", ruleActor);
                            startMenu.set("token", rs.getData().getToken());
                            startMenu.set("userId", rs.getData().getUid());
                            startMenu.set("worker", workState);

                          //  primaryStage.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    });

                }else {
                    // 弹框
                    Platform.runLater(() -> {

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("登录失败");
                        alert.setContentText(rs.getMsg());

                        alert.showAndWait();

                    });
                }
            });


            // ui 线程唤醒 工作线程处理工作
            synchronized (WorkState.LOCK) {
                WorkState.LOCK.notifyAll();
            }

        });
        primaryStage.setTitle("登陆");
        primaryStage.show();

    }
}
