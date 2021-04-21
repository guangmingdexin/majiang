package com.guang.majiang.event;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.Direction;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerNode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

/**
 * @ClassName TakeOutHandCardTask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/5 10:09
 * @Version 1.0
 **/
public class TakeOutHandCardTask implements Runnable {

    private PlayerNode<Player> p;

    private Pane pane;

    private volatile CardImage takeOutCard;

    private boolean isAi;

    public TakeOutHandCardTask(PlayerNode<Player> p, Pane pane, boolean isAi, CardImage takeOutCard) {
        this.p = p;
        this.pane = pane;
        this.isAi = isAi;
        this.takeOutCard = takeOutCard;
    }


    @Override
    public void run() {
        // 分为 ai 出牌以及 玩家本身出牌
        Player player = p.item;
        if(isAi) {
            // 说明为 ai 出牌 随机出一张牌
            List<CardImage> cards = player.getPlayerCard().getCards();
            int r = new Random().nextInt(cards.size());
            takeOutCard = cards.get(r);
            System.out.println("ai 出牌 " + takeOutCard.getValue());
            ImageView img = getImage(player, cards, r);
            cards.remove(r);
            pane.getChildren().remove(img);
            // 保证中线两边尽量有一样的牌数
            // 首先获取各个方位的中点, 统计中点两边的数量
            // 如果 left > right 显然向下移动
            // 如果 right > left 显然向上移动
            int left = 0;
            int right = 0;
            for (CardImage card : cards) {
                ImageView temp = null;
                if (player.getDirection() == Direction.LEFT) {
                    temp = card.getFaceDownImage().getFaceDownLeft();
                    if (temp.getY() > GlobalConstant.MIDDLEFTY) {
                        left++;
                    } else {
                        right++;
                    }
                } else if (player.getDirection() == Direction.ABOVE) {
                    temp = card.getFaceDownImage().getImageView();
                    if (temp.getX() < GlobalConstant.MIDDLEX) {
                        left++;
                    } else {
                        right++;
                    }
                } else if (player.getDirection() == Direction.RIGHT) {
                    temp = card.getFaceDownImage().getFaceDownRight();
                    if (temp.getY() > GlobalConstant.MIDDLEFTY) {
                        left++;
                    } else {
                        right++;
                    }
                }
            }
            if(right >= left) {
                for (int i = r; i < cards.size(); i++) {
                    ImageView cImage = getImage(player, cards, i);
                    if(player.getDirection() == Direction.LEFT || player.getDirection() == Direction.RIGHT) {
                        double height = cImage.getFitHeight();
                        double curY = cImage.getY();
                        cImage.setY(curY - height);
                    }else if(player.getDirection() == Direction.ABOVE) {
                        double width = cImage.getFitWidth();
                        double curX = cImage.getX();
                        cImage.setX(curX - width);
                    }
                }
            }else {
                for (int i = r - 1; i  >= 0 ; i--) {
                    ImageView cImage = getImage(player, cards, i);
                    if(player.getDirection() == Direction.LEFT || player.getDirection() == Direction.RIGHT) {
                        double height = cImage.getFitHeight();
                        double curY = cImage.getY();
                        cImage.setY(curY + height);
                    }else if(player.getDirection() == Direction.ABOVE) {
                        double width = cImage.getFitWidth();
                        double curX = cImage.getX();
                        cImage.setX(curX + width);
                    }
                }
            }
        }
        List<CardImage> usedCards = player.getPlayerCard().getUsedCards();
        usedCards.add(takeOutCard);
        int len = usedCards.size();
        ImageView takeOutHandCard = takeOutCard.getImageView();
        if(player.getDirection() == Direction.LEFT) {
            int startX = GlobalConstant.MIDDLEX - 7 * GlobalConstant.CARD_WIDTH;
            int startY = GlobalConstant.MIDDLEY - GlobalConstant.CARD_HEIGHT;
            double curX = startX - (len / 7) * GlobalConstant.CARD_HEIGHT;
            double curY = startY + (len % 7) * GlobalConstant.CARD_WIDTH;
            takeOutHandCard.setX(curX);
            takeOutHandCard.setY(curY);
            takeOutHandCard.setStyle("-fx-rotate: 90");
        }else if(player.getDirection() == Direction.ABOVE) {
            int startX = GlobalConstant.MIDDLEX - 5 * GlobalConstant.CARD_WIDTH;
            int startY = GlobalConstant.MIDDLEY - 2 * GlobalConstant.CARD_HEIGHT;
            double curX = startX + (len % 10) * GlobalConstant.CARD_WIDTH;
            double curY = startY - (len / 10) * GlobalConstant.CARD_HEIGHT;
            takeOutHandCard.setX(curX);
            takeOutHandCard.setY(curY);
            takeOutHandCard.setStyle("-fx-rotate: 180");
        }else if(player.getDirection() == Direction.RIGHT) {
            int startX = GlobalConstant.MIDDLEX + 6 * GlobalConstant.CARD_WIDTH;
            int startY = GlobalConstant.MIDDLEY -  GlobalConstant.CARD_HEIGHT;
            double curX = startX + (len / 5) * GlobalConstant.CARD_HEIGHT;
            double curY = startY + (len % 5) * GlobalConstant.CARD_WIDTH;
            takeOutHandCard.setX(curX);
            takeOutHandCard.setY(curY);
            takeOutHandCard.setStyle("-fx-rotate: 270");
        }else {
            return;
        }
        takeOutHandCard.setVisible(true);
    }

    private ImageView getImage(Player player, List<CardImage> cards, int r) {
        CardImage c = cards.get(r);
        ImageView img = null;
        if(player.getDirection() == Direction.LEFT) {
            img  = c.getFaceDownImage().getFaceDownLeft();
        }else if(player.getDirection() == Direction.ABOVE) {
            img = c.getFaceDownImage().getImageView();
        }else if(player.getDirection() == Direction.RIGHT){
            img = c.getFaceDownImage().getFaceDownRight();
        }else {
            System.out.println("方向错误！");
        }
        return img;
    }

    public CardImage getTakeOutCard() {
        return takeOutCard;
    }
}
