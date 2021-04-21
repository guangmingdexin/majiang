package com.guang.majiang.event;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * @ClassName PlayerTakeTask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/9 9:18
 * @Version 1.0
 **/
public class PlayerTakeTask implements Runnable {

    private Player player;

    private Pane pane;

    private CardImage handCard;

    public PlayerTakeTask(Player player, Pane pane, CardImage handCard) {
        this.player = player;
        this.pane = pane;
        this.handCard = handCard;
    }

    @Override
    public void run() {
        handCard.setCardStatus(CardStatus.HOLD);
        List<CardImage> cards = player.getPlayerCard().getCards();
        ImageView under = handCard.getImageView();
        pane.getChildren().add(under);
        // 使用插入排序
        // 可以使用二分
        int i = 0;
        for (; i < cards.size(); i++) {
            if(handCard.compareTo(cards.get(i)) <= 0) {
                break;
            }
        }
        // i == card.size()
        if(i < cards.size()) {

            // 获取 第 i + 1 个元素的 x,y
            double x = cards.get(i).getImageView().getX();
            double y = cards.get(i).getImageView().getY();

            under.setX(x);
            under.setY(y);

            ObservableList<Node> children = pane.getChildren();
            for (Node node : children) {
                if("arrow".equals(node.getId())) {
                    System.out.println("id: " + node.getId());
                    ImageView arrow = (ImageView) node;
                    arrow.setX(x + 10);
                    arrow.setY(y - 20);
                    arrow.setVisible(true);
                }
            }

            // 从第 i 个元素开始 从后移动
            for (int j = i; j < cards.size(); j++) {
                ImageView img = cards.get(j).getImageView();
                img.setX(img.getX() + GlobalConstant.CARD_WIDTH);
            }
            cards.add(i, handCard);
        }else if(i == cards.size()) {
            // 获取 第 i + 1 个元素的 x,y
            double x = cards.get(i - 1).getImageView().getX();
            double y = cards.get(i - 1).getImageView().getY();

            under.setX(x + GlobalConstant.CARD_WIDTH);
            under.setY(y);

            cards.add(handCard);
        }
    }
}
