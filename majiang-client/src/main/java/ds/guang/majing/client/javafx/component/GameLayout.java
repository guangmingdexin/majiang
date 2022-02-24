package ds.guang.majing.client.javafx.component;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;

/**
 * @author guangyong.deng
 * @date 2022-02-21 14:10
 */
public class GameLayout extends Application implements Layout {

    public static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getClassLoader().getResource("game_layout_2.fxml");
        System.out.println("resource: " + resource);
        Parent root = FXMLLoader.load(resource);
        scene = new Scene(root, 600, 500);

        // 1.获取整个屏幕大小
        // 2.获取单张麻将的所有属性（左，右，上）

        // 4：3
        // 将 屏幕高度 设置为 8 份，上下 各占 一份，中间占用 6 份

        // 将屏幕宽度 设置为 10 份 左右各一份 ，

        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();

        System.out.println("w: " + width + " h: " + height);

        primaryStage.setTitle("Simple JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    @Override
    public void set(String name, Object value) {

    }

    @Override
    public Object get(String name) {
        return null;
    }

}
