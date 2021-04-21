package com.guang.majiang.player;

import com.guang.majiang.image.CardImage;
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
public class PlayerCard {

    // 手牌
    private List<CardImage> cards;

    // 已经使用的牌
    private List<CardImage> usedCards;

    // 碰牌 杠牌
    private List<CardImage[]> bump;

    public PlayerCard(List<CardImage> cards, List<CardImage> usedCards,
                      List<CardImage[]> bump) {
        this.cards = cards;
        this.usedCards = usedCards;
        this.bump = bump;
    }

}
