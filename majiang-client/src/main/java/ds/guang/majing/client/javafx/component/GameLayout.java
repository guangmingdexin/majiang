package ds.guang.majing.client.javafx.component;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * 游戏布局
 *
 * @author asus
 */
public class GameLayout extends Application implements Layout {

    private Stage stage;

    @Override
    public void set(String name, Object value) {

    }

    @Override
    public Object get(String name) {
        return null;
    }

    @Override
    public void close() {
        stage.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage;


    }
}
