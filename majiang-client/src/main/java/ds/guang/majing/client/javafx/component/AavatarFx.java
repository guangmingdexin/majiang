package ds.guang.majing.client.javafx.component;

import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author guangyong.deng
 * @date 2022-02-21 14:25
 */
public class AavatarFx {


    private Image src;


    private StackPane avatar;


    public AavatarFx(String url) {

        avatar = new StackPane();

        avatar.setPrefWidth(30);
        avatar.setPrefHeight(30);
        avatar.setPadding(new Insets(5));

        DropShadow effect = new DropShadow();
        effect.setRadius(5);
        effect.setOffsetY(2.0);
        avatar.setEffect(effect);

        src = new Image(url);

        ImageView view = new ImageView(src);
        view.setFitHeight(30);
        view.setFitWidth(30);

        Circle circle = new Circle();
        circle.setFill(Paint.valueOf("aqua"));
        circle.setCenterX(15);
        circle.setCenterY(15);
        circle.setRadius(15);

        view.setClip(circle);
    }
}
