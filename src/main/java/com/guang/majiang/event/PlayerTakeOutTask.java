package com.guang.majiang.event;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.Direction;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * @ClassName PlayerTakeOutTask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/9 8:36
 * @Version 1.0
 **/
public class PlayerTakeOutTask implements Runnable {

    private Player player;

    private CardImage c;

    public PlayerTakeOutTask(Player player, CardImage c) {
        this.player = player;
        this.c = c;
    }

    @Override
    public void run() {
        List<CardImage> usedCards = player.getPlayerCard().getUsedCards();
        List<CardImage> cards = player.getPlayerCard().getCards();
        int r = cards.indexOf(c);
        cards.remove(c);
        usedCards.add(c);
        int len = usedCards.size();
        int startX = GlobalConstant.MIDDLEX - 5 * GlobalConstant.CARD_WIDTH;
        int startY = GlobalConstant.MIDDLEY + 2 * GlobalConstant.CARD_HEIGHT;

        // 计算 应该摆放位置
        int curX = startX + (len % 10) * GlobalConstant.CARD_WIDTH;
        int curY = startY + (len / 10) * GlobalConstant.CARD_HEIGHT;

        c.getImageView().setX(curX);
        c.getImageView().setY(curY);
        c.setCardStatus(CardStatus.OUT);

        int left = 0;
        int right = 0;
        for (CardImage card : cards) {
            if (player.getDirection() == Direction.UNDER) {
                if(card.getImageView().getX() < getMiddleX()) {
                    left ++;
                }else  {
                    right ++;
                }
            }
        }
        if(right >= left) {
            for (int i = r; i < cards.size(); i++) {
                ImageView cImage = cards.get(i).getImageView();
                double width = cImage.getFitWidth();
                double oldX = cImage.getX();
                cImage.setX(oldX - width);
            }
        }else {
            for (int i = r - 1; i  >= 0 ; i--) {
                ImageView cImage = cards.get(i).getImageView();
                double width = cImage.getFitWidth();
                double oldX = cImage.getX();
                cImage.setX(oldX + width);
            }
        }
    }


    private double getMiddleX() {
        List<CardImage[]> bumps = player.getPlayerCard().getBump();
        if(bumps.size() == 0) {
            return GlobalConstant.MIDDLEX;
        }
        CardImage[] images = bumps.get(bumps.size() - 1);
        double start = images[images.length - 1].getImageView().getX();

        return (GlobalConstant.BG_WEITH - start) / 2;
    }
}
