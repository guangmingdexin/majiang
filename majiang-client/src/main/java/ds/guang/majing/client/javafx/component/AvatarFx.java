package ds.guang.majing.client.javafx.component;

import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author guangyong.deng
 * @date 2022-02-25 11:50
 */
public class AvatarFx extends Cell {


    public static Node build() {

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(5);

        ImageView imageView = new ImageView(new Image("default.jpg"));

        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        Circle circle = new Circle();
        circle.setCenterX(15);
        circle.setCenterY(15);
        circle.setRadius(15);
        circle.setFill(Paint.valueOf("aqua"));

        imageView.setClip(circle);
        return imageView;

    }
}
