package com.guang.majiang.event;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.Direction;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerNode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName InitHandCardTask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/3 19:22
 * @Version 1.0
 **/
public class InitHandCardTask implements Runnable {

    private PlayerNode<Player> p;

    private Player player;

    private Pane pane;

    // 所有手牌
    private LinkedList<CardImage> totalCards;

    private LinkedBlockingQueue<Runnable> taskQueue;

    private LinkedBlockingQueue<Runnable> playerEventTaskQueue;

    public InitHandCardTask(PlayerNode<Player> p, Pane pane,
                            LinkedList<CardImage> cards,
                            LinkedBlockingQueue<Runnable> taskQueue,
                            LinkedBlockingQueue<Runnable> playerEventTaskQueue) {
        this.p = p;
        this.player = p.item;
        this.pane = pane;
        this.totalCards = cards;
        this.taskQueue = taskQueue;
        this.playerEventTaskQueue = playerEventTaskQueue;
    }

    @Override
    public void run() {

        // 给玩家添加手牌
        int maxCards = player.isBookmaker() ? 14 : 13;
        List<CardImage> cards = player.getPlayerCard().getCards();

        if(cards.size() < maxCards) {
            if(player.getDirection() == Direction.UNDER) {
                // 一次摸 4张牌
                for (int i = 0; i < 4 && cards.size() < maxCards && !totalCards.isEmpty(); i++) {
                    ImageView imgCard = getImage(cards, player);
                    // 设置牌的 x y
                    imgCard.setX(GlobalConstant.getStart(GlobalConstant.BG_WEITH, GlobalConstant.MAX_TOTAL_WIDTH) +
                            cards.size() * GlobalConstant.CARD_WIDTH );
                    imgCard.setY(GlobalConstant.BG_HEIGHT - GlobalConstant.CARD_HEIGHT - 20);
                }
            }else if(player.getDirection() == Direction.LEFT) {

                for (int i = 0; i < 4 && cards.size() < maxCards && !totalCards.isEmpty(); i++) {

                    ImageView imgCard = getImage(cards, player);
                    double x = 60;
                    double y = GlobalConstant.getStart(GlobalConstant.BG_HEIGHT, GlobalConstant.MAX_TOTAL_HIGHT) +
                            cards.size() * GlobalConstant.FACE_DOWN_LEFT_HEIGHT;

                    imgCard.setX(x);
                    imgCard.setY(y);
                }
            }else if(player.getDirection() == Direction.ABOVE) {
                for (int i = 0; i < 4 && cards.size() < maxCards && !totalCards.isEmpty(); i++) {
                    ImageView imgCard = getImage(cards, player);
                    double x = GlobalConstant.getStart(GlobalConstant.BG_WEITH, GlobalConstant.MAX_TOTAL_WIDTH) +
                            cards.size() * GlobalConstant.CARD_WIDTH;
                    double y = GlobalConstant.CARD_HEIGHT + 20;
                    imgCard.setX(x);
                    imgCard.setY(y);
                }
            }else {
                for (int i = 0; i < 4 && cards.size() < maxCards && !totalCards.isEmpty(); i++) {
                    ImageView imgCard = getImage(cards, player);
                    int x = GlobalConstant.BG_WEITH - 120;
                    double y = GlobalConstant.getStart(GlobalConstant.BG_HEIGHT, GlobalConstant.MAX_TOTAL_HIGHT) +
                            cards.size() * GlobalConstant.FACE_DOWN_LEFT_HEIGHT;
                    imgCard.setX(x);
                    imgCard.setY(y);
                }
            }
        }
    }

    private ImageView getImage(List<CardImage> cards, Player player) {
        if(totalCards.isEmpty()) {
            throw new NullPointerException("没有牌了！初始化手牌错误");
        }
        CardImage c = totalCards.poll();
        cards.add(c);
        c.setCardStatus(CardStatus.HOLD);
        ImageView img = null;
        if(player.getDirection() == Direction.UNDER) {
            img = c.getImageView();
            img.setOnMouseClicked(new CardHandler(p, c, playerEventTaskQueue));
        }else if(player.getDirection() == Direction.LEFT) {
            img = c.getFaceDownImage().getFaceDownLeft();
        }else if(player.getDirection() == Direction.ABOVE) {
            img = c.getFaceDownImage().getImageView();
        }else if(player.getDirection() == Direction.RIGHT) {
            img = c.getFaceDownImage().getFaceDownRight();
        }
        pane.getChildren().add(img);
        if(player.getDirection() != Direction.UNDER) {
            pane.getChildren().add(c.getImageView());
            c.getImageView().setVisible(false);
        }
        return img;
    }
}
