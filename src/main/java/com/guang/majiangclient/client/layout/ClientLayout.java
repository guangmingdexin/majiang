package com.guang.majiangclient.client.layout;

import com.guang.majiang.image.MyImage;
import com.guang.majiang.layout.SimpleInit;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @ClassName ClientLayout
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/23 9:31
 * @Version 1.0
 **/
public class ClientLayout extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        SimpleInit init = new SimpleInit();
        // 1.加载背景图片
        MyImage bg = init.getBg();
        pane.getChildren().add(bg.getImageView());
    }
}
