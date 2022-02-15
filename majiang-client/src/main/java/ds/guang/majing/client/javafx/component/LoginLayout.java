package ds.guang.majing.client.javafx.component;


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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 *  登录界面
 *
 * @author guangmingdexin
 * @date 2021/4/16 16:05
 **/
public class LoginLayout extends Application {

    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
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

        login.setOnAction(e -> {
            System.out.println("登陆！");
            String tel = userTextField.getText();
            String pwd = pwBox.getText();

        });
        primaryStage.setTitle("登陆");
        primaryStage.show();


    }
}
