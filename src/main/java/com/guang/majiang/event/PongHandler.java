package com.guang.majiang.event;

import com.guang.majiang.common.Direction;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.common.SpecialEvent;
import com.guang.majiang.image.CardImage;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.Iterator;

/**
 * @ClassName PongHandler
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/16 20:31
 * @Version 1.0
 **/
public class PongHandler implements EventHandler<MouseEvent> {

    private SpecialEventTask task;

    public PongHandler(SpecialEventTask task) {
        this.task = task;
    }

    @Override
    public void handle(MouseEvent event) {

        task.player.getPlayerCard().getUsedCards().remove(task.takeOut);
        // 将 三张牌连在一起
        CardImage[] pong = new CardImage[3];
        pong[0] = task.takeOut;
        if(task.player.getDirection() == Direction.LEFT) {
            pong[0].getImageView().setStyle("-fx-rotate: 180");
        }else if(task.player.getDirection() == Direction.ABOVE) {
            pong[0].getImageView().setStyle("-fx-rotate: 0");
        }else if(task.player.getDirection() == Direction.RIGHT) {
            pong[0].getImageView().setStyle("-fx-rotate: 360");
        }
        // 获取起始 x y
        double x = 0;
        double y = 0;
        if(task.bump.size() > 0) {
            // 获取 bump 最后一个元素 的 x
            CardImage[] images = task.bump.get(task.bump.size() - 1);
            x = images[images.length - 1].getImageView().getX() + GlobalConstant.CARD_WIDTH + 30;
            y = images[0].getImageView().getY();
        }else {
            x =  3 * GlobalConstant.CARD_WIDTH + 30;
            y = task.handCards.get(0).getImageView().getY();
        }
        pong[0].getImageView().setX(x);
        pong[0].getImageView().setY(y);
        int i = 1;
        Iterator<CardImage> iterator = task.handCards.iterator();

        while (iterator.hasNext()) {
            CardImage card = iterator.next();
            if(card.compareTo(task.takeOut) == 0) {
                // 将 card 从手牌中移除
                // 首先查找到位置
                pong[i++] = card;
                iterator.remove();
            }
        }

        for (int j = 0; j < task.handCards.size() - 1; j++) {
            ImageView cur = task.handCards.get(j).getImageView();
            ImageView next = task.handCards.get(j + 1).getImageView();
            if(Double.compare((cur.getX() + 126.0), next.getX()) == 0) {
                for (int k = 0; k <= j; k++) {
                    ImageView temp = task.handCards.get(k).getImageView();
                    temp.setX(temp.getX() + 2 * GlobalConstant.CARD_WIDTH);
                }
            }
        }

        for (int j = 1; j < pong.length; j++) {
            ImageView p = pong[j].getImageView();
            p.setX(pong[j - 1].getImageView().getX() + GlobalConstant.CARD_WIDTH);
        }
        task.bump.add(pong);

        for (ImageView eventImage : task.eventImages) {
            eventImage.setVisible(false);
            eventImage.setOnMouseClicked(null);
        }
        task.event = SpecialEvent.PONG;
        task.over = true;
    }
}
