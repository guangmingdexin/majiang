package com.guang.majiangclient.client.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.handle.click.RegisterTakeOutEvent;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName GameInfoUser
 * @Author guangmingdexin
 * @Date 2021/5/22 16:13
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
public class GameInfoCard implements Serializable {

    // 手牌
    @JsonIgnore
    private List<Integer> useCards;

    private List<Integer> takeOutCards;

    private LinkedHashMap<Integer, Integer> pgNums;

    /**
     * pong - gang
     */
    private transient LinkedHashMap<Integer, CardImage[]> pg;

    @JsonIgnore
    private transient List<CardImage> cardImages;

    @JsonIgnore
    private transient List<CardImage> takeOutCarsImages;

    private int size;

    // 玩家回合 id
    private Direction aroundPlayerDire;

    private Direction cur;

    private boolean hu;


    public GameInfoCard(List<Integer> useCards, int size, Direction aroundPlayerDire,
                        Direction cur) {
        this.useCards = useCards;
        this.size = size;
        this.aroundPlayerDire = aroundPlayerDire;
        this.cur = cur;
        this.takeOutCards = new ArrayList<>();
        this.pgNums = new LinkedHashMap<>();
    }

    public void buildCard(List<Integer> useCards, List<ImageView> views, long roomId, long userId) {
        if(cardImages == null) {
            cardImages = new ArrayList<>();
            for (int i = 0; i < useCards.size(); i++) {
                ImageView view = views.get(i);
                CardImage cardImage = new CardImage().build(useCards.get(i), 0, view);
                cardImages.add(cardImage);
                // TODO 添加事件
                view.setOnMouseClicked(new RegisterTakeOutEvent(
                        cardImage,
                        useCards,
                        cardImages,
                        takeOutCards,
                        takeOutCarsImages,
                        userId,
                        roomId));
            }
        }
    }

    public void buildCardDown() {
        if(cardImages == null) {
            cardImages = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                CardImage cardImage = new CardImage().buildDown();
                cardImages.add(cardImage);
            }
        }
    }

    public boolean around() {
        if(aroundPlayerDire == null || cur == null) {
            return false;
        }
        return aroundPlayerDire == cur;
    }

    @Override
    public String toString() {
        return "GameInfoCard{" +
                "useCards=" + useCards +
                ", takeOutCards=" + takeOutCards +
                ", pgNums=" + pgNums +
                ", pg=" + pg +
                ", takeOutCarsImages=" + takeOutCarsImages +
                ", aroundPlayerDire=" + aroundPlayerDire +
                ", cur=" + cur +
                ", hu=" + hu +
                '}';
    }
}
