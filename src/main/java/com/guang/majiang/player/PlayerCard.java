package com.guang.majiang.player;

import com.guang.majiang.event.CardEvent;
import com.guang.majiang.event.CardHandler;
import com.guang.majiang.event.SimpleOperationFunc;
import com.guang.majiang.image.CardImage;
import javafx.event.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName PlayerCard
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/13 20:04
 * @Version 1.0
 **/
@Getter
@Setter
public class PlayerCard implements CardEvent {

    private Player player;

    // 手牌
    private List<CardImage> cards;

    // 已经使用的牌
    private List<CardImage> usedCards;

    // 碰牌 杠牌
    private List<CardImage[]> bump;

    private PlayerCard[] playerCards;

    private volatile CardImage oldCard;

    public PlayerCard(Player player, List<CardImage> cards, List<CardImage> usedCards,
                      List<CardImage[]> bump, PlayerCard[] playerCards) {
        this.player = player;
        this.cards = cards;
        this.usedCards = usedCards;
        this.bump = bump;
        this.playerCards = playerCards;
    }

    @Override
    public void start() {

        SimpleOperationFunc func = new SimpleOperationFunc();
        // 对当前所有手牌进行事件初始化
        for (CardImage c : cards) {
            c.getImageView().setOnMouseClicked(new CardHandler(func, c, this));
        }

    }


    @Override
    public void shutdown() {

    }



    @Override
    public void addCardHand(String id) {

    }

    @Override
    public void register(EventHandler handler) {

    }

}
