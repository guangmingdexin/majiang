package com.guang.majiang.event;

import com.guang.majiang.common.Direction;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * @ClassName TakeHandCardTask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/9 9:15
 * @Version 1.0
 **/
public class TakeHandCardTask implements Runnable {

    private Player player;

    private Pane pane;

    private CardImage handCard;

    public TakeHandCardTask(Player player, Pane pane, CardImage handCard) {
        this.player = player;
        this.pane = pane;
        this.handCard = handCard;
    }

    @Override
    public void run() {
        List<CardImage> cards = player.getPlayerCard().getCards();
        CardImage fin = cards.get(cards.size() - 1);
        cards.add(handCard);
        if(player.getDirection() == Direction.LEFT) {
            ImageView left = handCard.getFaceDownImage().getFaceDownLeft();
            left.setX(fin.getFaceDownImage().getFaceDownLeft().getX());
            left.setY(fin.getFaceDownImage().getFaceDownLeft().getY() + GlobalConstant.FACE_DOWN_LEFT_HEIGHT);
            pane.getChildren().add(left);
        }else if(player.getDirection() == Direction.ABOVE) {
            ImageView above = handCard.getFaceDownImage().getImageView();
            above.setY(fin.getFaceDownImage().getImageView().getY());
            above.setX(fin.getFaceDownImage().getImageView().getX() + GlobalConstant.CARD_WIDTH);
            pane.getChildren().add(above);
        }else if(player.getDirection() == Direction.RIGHT) {
            ImageView right = handCard.getFaceDownImage().getFaceDownRight();
            right.setX(fin.getFaceDownImage().getFaceDownRight().getX());
            right.setY(fin.getFaceDownImage().getFaceDownRight().getY() + GlobalConstant.FACE_DOWN_LEFT_HEIGHT);
            pane.getChildren().add(right);
        }
        pane.getChildren().add(handCard.getImageView());
        handCard.getImageView().setVisible(false);
    }
}
