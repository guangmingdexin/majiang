package com.guang.majiang.layout;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.Direction;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerCard;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;

import java.util.*;

/**
 *   封装为 单独的一个类
 *   用来是实现界面逻辑
 * @ClassName UIInit
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/2/4 20:23
 * @Version 1.0
 **/
public class UIInit {

    // 静态方法
    // 渲染手牌
    public static Map<String, List<Node>> initHandCard(PlayerCard playerCard, boolean isSort) {

        Map<String, List<Node>> map = new HashMap<>(2);

        List<CardImage> cards = playerCard.getCards();

        Player player = playerCard.getPlayer();

        int size = cards.size();

        List<Node> res = new ArrayList<>();

        List<Node> otherCard = new ArrayList<>();

        // 首先判断玩家方位
        // 加载自己的手牌
        if(Direction.UNDER == player.getDirection()) {

            // 如果是庄家 则直接一次性 加载两张牌，并整理

            // 如果不是庄家 则先单独加载一张牌，并整理

            // 摸牌之后出牌需要整理棋牌

            // 排序规则需要自定义
            for(int i = 0; i < size; i++) {

                // 最后一张牌隔一个距离
                double endFlag = i == size - 1 && !isSort ? GlobalConstant.CARD_WIDTH : 0;

                ImageView card = cards.get(i).getImageView();

                card.setX(getStart(GlobalConstant.BG_WEITH, GlobalConstant.MAX_TOTAL_WIDTH) + i * GlobalConstant.CARD_WIDTH + endFlag);
                card.setY(GlobalConstant.BG_HEIGHT - GlobalConstant.CARD_HEIGHT - 20);

                res.add(card);
            }

        }else if(Direction.LEFT == player.getDirection()) {
            // 返回左边玩家手牌
            for (int i = 0; i < size; i++) {
                // 图片对象
                ImageView cardFaceDown = cards.get(i).
                        getFaceDownImage().getFaceDownLeft();

                ImageView card = cards.get(i).getImageView();

                // x 起始位置
                double x = 60;
                double y = getStart(GlobalConstant.BG_HEIGHT, GlobalConstant.MAX_TOTAL_HIGHT) + i * GlobalConstant.FACE_DOWN_LEFT_HEIGHT;

                cardFaceDown.setX(x);
                cardFaceDown.setY(y);

                card.setVisible(false);

                res.add(cardFaceDown);
                otherCard.add(card);

            }
        }else if(Direction.ABOVE == player.getDirection()) {

            for(int i = 0; i < size; i++) {

                ImageView cardFaceDown = cards.get(i).
                        getFaceDownImage().getImageView();
                ImageView card = cards.get(i).getImageView();

                double x = getStart(GlobalConstant.BG_WEITH, GlobalConstant.MAX_TOTAL_WIDTH) + i * GlobalConstant.CARD_WIDTH;
                double y = GlobalConstant.CARD_HEIGHT + 20;

                cardFaceDown.setX(x);
                cardFaceDown.setY(y);

                card.setVisible(false);

                res.add(cardFaceDown);
                otherCard.add(card);
            }

        }else if(Direction.RIGHT == player.getDirection()) {

            for (int i = 0; i < size; i++) {
                // 图片对象
                ImageView cardFaceDown = cards.get(i).
                        getFaceDownImage().getFaceDownRight();
                ImageView card = cards.get(i).getImageView();

                int x = GlobalConstant.BG_WEITH - 120;
                double y = getStart(GlobalConstant.BG_HEIGHT, GlobalConstant.MAX_TOTAL_HIGHT) + i * GlobalConstant.FACE_DOWN_LEFT_HEIGHT;

                // x 起始位置
                cardFaceDown.setX(x);
                cardFaceDown.setY(y);

                card.setVisible(false);

                res.add(cardFaceDown);
                otherCard.add(card);
            }
        }

        map.put("neg", res);
        map.put("pos", otherCard);

        return map;

    }


    public static void aiHandCard(PlayerCard playerCard) {

        // 首先模拟随机出牌
        List<CardImage> cards = playerCard.getCards();

        if(cards.size() <= 0) {
            System.out.println("没牌了！");
            return;
        }
        int r = new Random().nextInt(cards.size());

        CardImage c = cards.get(r);

        Player p = playerCard.getPlayer();

        int curX = 0;

        int curY = 0;

        ImageView faceDown = c.getImageView();

        faceDown.setVisible(true);

        if(p.getDirection() == Direction.ABOVE) {

            System.out.println("变换位置！");

            int startX = GlobalConstant.MIDDLEX - 5 * GlobalConstant.CARD_WIDTH;
            int startY = GlobalConstant.MIDDLEY - 2 * GlobalConstant.CARD_HEIGHT;

            curX = startX + (playerCard.getUsedCards().size() % 10) * GlobalConstant.CARD_WIDTH;

            curY = startY + (playerCard.getUsedCards().size() / 10) * GlobalConstant.CARD_HEIGHT;

            faceDown.setStyle("-fx-rotate: 180");

            c.getFaceDownImage().getImageView().setVisible(false);

        }else if(p.getDirection() == Direction.LEFT) {

            int startX = GlobalConstant.MIDDLEX - 7 * GlobalConstant.CARD_WIDTH;
            int startY = GlobalConstant.MIDDLEY -  GlobalConstant.CARD_HEIGHT;

            curX = startX - (playerCard.getUsedCards().size() / 5) * GlobalConstant.CARD_HEIGHT;
            curY = startY + (playerCard.getUsedCards().size() % 5) * GlobalConstant.CARD_WIDTH;

            faceDown.setStyle("-fx-rotate: 90");

            c.getFaceDownImage().getFaceDownLeft().setVisible(false);

        }else if(p.getDirection() == Direction.RIGHT) {

            int startX = GlobalConstant.MIDDLEX + 6 * GlobalConstant.CARD_WIDTH;
            int startY = GlobalConstant.MIDDLEY -  GlobalConstant.CARD_HEIGHT;

            curX = startX + (playerCard.getUsedCards().size() / 5) * GlobalConstant.CARD_HEIGHT;
            curY = startY + (playerCard.getUsedCards().size() % 5) * GlobalConstant.CARD_WIDTH;

            faceDown.setStyle("-fx-rotate: 270");
            c.getFaceDownImage().getFaceDownRight().setVisible(false);

        }
        System.out.println(p.getName() + "ai 出了一张牌 " + r + " " + p.getDirection().toString());

        faceDown.setX(curX);
        faceDown.setY(curY);

        c.setCardStatus(CardStatus.OUT);
        playerCard.getUsedCards().add(c);
        cards.remove(c);

        UIInit.clearHandCard(p, cards, c, r);
    }


    private static double getStart(double width, double maxWidth) {
        return Math.abs(width - maxWidth) / 2;
    }


    public static void clearHandCard(Player player, List<CardImage> cards, CardImage c, int target) {

        Direction p = player.getDirection();

        if(p == Direction.UNDER) {

            if(target == cards.size() + 1) {
                return;
            }

            // 需要始终保持 手牌在中间
            // 1. target > (cards.size() + 1) / 2 牌向左移动
            // 2. target < (cards.size() + 1) / 2 牌向右移动
            // 3. target == (cards.size + 1) / 2  牌向左移动

            if(target >= (cards.size() + 1) / 2) {
                for (int i = target; i < cards.size(); i++) {
                    ImageView card =  cards.get(i).getImageView();
                    double oldX = card.getX();
                    card.setX(oldX - GlobalConstant.CARD_WIDTH);
                }
            }else {
                for (int i = target - 1; i >= 0; i--) {
                    ImageView card =  cards.get(i).getImageView();
                    double oldX = card.getX();
                    card.setX(oldX + GlobalConstant.CARD_WIDTH);
                }
            }
        }else if(p == Direction.ABOVE) {
            if(target >= (cards.size() + 1) / 2) {
                for (int i = target; i < cards.size(); i++) {
                    ImageView card = cards.get(i).getFaceDownImage().getImageView();
                    double oldX = card.getX();
                    card.setX(oldX - GlobalConstant.CARD_WIDTH);
                }
            }else {
                for (int i = target - 1; i >= 0; i--) {
                    ImageView card = cards.get(i).getFaceDownImage().getImageView();
                    double oldX = card.getX();
                    card.setX(oldX + GlobalConstant.CARD_WIDTH);
                }
            }
        }else if(p == Direction.LEFT ) {
            ImageView oldFaceDownLeft = c.getFaceDownImage().getFaceDownLeft();
            if(target >= (cards.size() + 1) / 2) {
                for (int i = target; i < cards.size(); i++) {
                    ImageView faceDownLeft = cards.get(i).getFaceDownImage().getFaceDownLeft();
                    double oldY = faceDownLeft.getY();
                    faceDownLeft.setY(oldY - oldFaceDownLeft.getFitHeight());
                }
            }else {
                for (int i = target - 1; i >= 0; i--) {
                    ImageView faceDownLeft = cards.get(i).getFaceDownImage().getFaceDownLeft();
                    double oldY = faceDownLeft.getY();
                    faceDownLeft.setY(oldY + oldFaceDownLeft.getFitHeight());
                }
            }
        }else {
            ImageView oldFaceDownLeft = c.getFaceDownImage().getFaceDownRight();
            if(target >= (cards.size() + 1) / 2) {
                for (int i = target; i < cards.size(); i++) {
                    ImageView faceDownRight = cards.get(i).getFaceDownImage().getFaceDownRight();
                    double oldY = faceDownRight.getY();
                    faceDownRight.setY(oldY - oldFaceDownLeft.getFitHeight());
                }
            }else {
                for (int i = target - 1; i >= 0; i--) {
                    ImageView faceDownRight = cards.get(i).getFaceDownImage().getFaceDownRight();
                    double oldY = faceDownRight.getY();
                    faceDownRight.setY(oldY + oldFaceDownLeft.getFitHeight());
                }
            }
        }

    }

}
