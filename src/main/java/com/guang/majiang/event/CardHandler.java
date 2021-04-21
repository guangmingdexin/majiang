package com.guang.majiang.event;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerNode;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName CardHandler
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/2/2 19:07
 * @Version 1.0
 **/

public class CardHandler implements EventHandler<MouseEvent> {

    private PlayerNode<Player> p;

    private CardImage c;

    private LinkedBlockingQueue<Runnable> product;

    public CardHandler(PlayerNode<Player> p, CardImage c, LinkedBlockingQueue<Runnable> product) {
        this.p = p;
        this.c = c;
        this.product = product;
    }

    @SneakyThrows
    @Override
    public void handle(MouseEvent event) {
        Player player = p.item;
        List<CardImage> cards = player.getPlayerCard().getCards();
        // 首先判断当前状态
        if (c.getCardStatus() == CardStatus.STORAGE) {
            System.out.println("点击了一张无效的牌！");
            // 此时无法点击
        } else if (c.getCardStatus() == CardStatus.HOLD) {

            // 首先判断是否还有其他牌的状态也为 READY
            for (CardImage card : cards) {
                if (card.getCardStatus() == CardStatus.READY &&
                        !card.equals(c)) {
                    // 如果有 将该牌 取消 READY 状态
                    card.getImageView().setY(card.getImageView().getY() + 20);
                    card.setCardStatus(CardStatus.HOLD);
                }
            }
            // 牌已经在玩家手中，此时点击鼠标 进入下一个状态
            c.getImageView().setY(c.getImageView().getY() - 20);
            c.setCardStatus(CardStatus.READY);
        }else if (c.getCardStatus() == CardStatus.READY) {
            if(player.getIsRound() != 1) {
                return;
            }
            if(product.isEmpty()) {
                product.offer(new PlayerTakeOutTask(player, c));
            }
        }
    }
}
