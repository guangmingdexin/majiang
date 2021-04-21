package com.guang.majiangclient.client.layout.show;

import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.service.Service;
import com.guang.majiangclient.client.util.ConfigOperation;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @ClassName LoginLayout
 * @Description 登录界面
 * @Author guangmingdexin
 * @Date 2021/4/16 16:05
 * @Version 1.0
 **/
public class LoginLayout extends Application {
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

        Label userName = new Label("手机号:");
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
        grid.add(hb, 1, 4);

        Button register = new Button("注册");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(register);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        Service center = ConfigOperation.getCenter();
        register.setOnAction(e -> {
            // actiontarget.setFill(Color.FIREBRICK);
            // actiontarget.setText("Sign in button pressed");
            // TODO 参数校验
            // 执行注册事件
            String tel = userTextField.getText();
            String pwd = pwBox.getText();
            center.submit(new Task<>(Event.REGISTER, new User(tel, pwd)));
        });

        login.setOnAction(e -> {
            String tel = userTextField.getText();
            String pwd = pwBox.getText();
            center.submit(new Task<>(Event.LOGIN, new User(tel, pwd)));
        });
        primaryStage.setTitle("登陆");
        primaryStage.show();

        // 启动客户端，连接服务器
        ConfigOperation.configInit();
    }
}
