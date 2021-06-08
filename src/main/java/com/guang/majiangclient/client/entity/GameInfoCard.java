package com.guang.majiangclient.client.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
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


    public GameInfoCard(List<Integer> useCards, int size, Direction aroundPlayerDire,
                        Direction cur) {
        this.useCards = useCards;
        this.size = size;
        this.aroundPlayerDire = aroundPlayerDire;
        this.cur = cur;
        this.takeOutCards = new ArrayList<>();
    }

    public GameInfoCard(int size) {
        this.size = size;
    }

    public void buildCard(List<Integer> useCards, List<ImageView> views, long roomId, long userId) {
        if(cardImages == null) {
            cardImages = new ArrayList<>();
            Service center = ConfigOperation.getCenter();
            for (int i = 0; i < useCards.size(); i++) {
                ImageView view = views.get(i);
                CardImage cardImage = new CardImage().build(useCards.get(i), 0, view);
                cardImages.add(cardImage);
                // TODO 添加事件
                view.setOnMouseClicked(event -> {
                    if(cardImage.getFlag() == 0) {
                        // 进入准备出牌状态
                        // 如果有其他手牌也进入出牌状态，则先将其
                        for (CardImage card : cardImages) {
                            if(card != cardImage && card.getFlag() == 1) {
                                ImageView cardImageView = card.getCard();
                                cardImageView.setTranslateY(cardImageView.getTranslateY() + 20);
                                card.setFlag(0);
                            }
                        }
                        ImageView cardImageView = cardImage.getCard();
                        cardImageView.setTranslateY(cardImageView.getTranslateY() - 20);
                        cardImage.setFlag(1);
                    }else if(cardImage.getFlag() == 1 && cur == aroundPlayerDire) {
                        // 出牌
                        // TODO 分为两步，第一步 向服务器发出请求
                        // 第二步，收到服务器 响应 渲染到本地
                        center.submit(
                                new Task<>(
                                    Event.RANDOMGAME,
                                    new GameInfoRequest(
                                            new PlayGameInfo(cardImage.getValue(),
                                                    GameEvent.TakeOutCard,
                                                    roomId, userId)
                                                )),
                                RandomMatchRequestMessage.class,
                                Event.RANDOMGAME);

                        // 本地比较渲染
                        int index = this.cardImages.indexOf(cardImage);
                        System.out.println("index: " + index);
                        System.out.println("cardImages: " + cardImage);
                        this.cardImages.remove(cardImage);
                       //  Removes the element at the specified position in this list
                        this.takeOutCards.add(useCards.remove(index));
                        if(this.takeOutCarsImages == null) {
                            this.takeOutCarsImages = new ArrayList<>();
                        }
                        this.takeOutCarsImages.add(cardImage);
                        cardImage.setFlag(2);
                    }
                });
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
                ", pg=" + pg +
                '}';
    }
}
