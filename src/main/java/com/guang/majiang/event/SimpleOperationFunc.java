package com.guang.majiang.event;


import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerCard;
import javafx.scene.image.ImageView;

/**
 * @ClassName SimpleOperationFunc
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/16 16:12
 * @Version 1.0
 **/
public class SimpleOperationFunc implements OperationFunc {

    @Override
    public void readyOn(ImageView imageView) {
        imageView.setY(imageView.getY() - 20);
    }

    @Override
    public void readyDown(ImageView imageView) {
        imageView.setY(imageView.getY() + 20);
    }

    @Override
    public void playCard(PlayerCard playerCard) {

    }
}
