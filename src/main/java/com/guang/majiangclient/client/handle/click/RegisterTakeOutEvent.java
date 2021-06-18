package com.guang.majiangclient.client.handle.click;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.CardImage;
import com.guang.majiangclient.client.entity.GameInfoCard;
import com.guang.majiangclient.client.entity.GameInfoRequest;
import com.guang.majiangclient.client.entity.PlayGameInfo;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @ClassName RegisterTakeOutEvent
 * @Author guangmingdexin
 * @Date 2021/6/11 15:09
 * @Version 1.0
 **/
@AllArgsConstructor
public class RegisterTakeOutEvent implements EventHandler<MouseEvent> {

    private CardImage takeCardImage;

    private List<Integer> useCards;

    private List<CardImage> cardImages;

    private List<Integer> takeOutCards;

    private List<CardImage> takeOutCarsImages;

    private long userId;

    private long roomId;

    @Override
    public void handle(MouseEvent event) {
        ClientLayout.gBottomGd.setLayoutY(641.0);
        if(takeCardImage.getFlag() == 0) {
            // 进入准备出牌状态
            // 如果有其他手牌也进入出牌状态，则先将其
            for (CardImage card : cardImages) {
                if(card != takeCardImage && card.getFlag() == 1) {
                    ImageView cardImageView = card.getCard();
                    cardImageView.setTranslateY(cardImageView.getTranslateY() + 20);
                    card.setFlag(0);
                }
            }
            ImageView cardImageView = takeCardImage.getCard();
            cardImageView.setTranslateY(cardImageView.getTranslateY() - 20);
            takeCardImage.setFlag(1);
        }else if(takeCardImage.getFlag() == 1 && CacheUtil.around() && (!CacheUtil.getSpecialEvent())) {
            // 出牌
            // 第二步，收到服务器 响应 渲染到本地
            Service center = ConfigOperation.getCenter();
            center.submit(
                    new Task<>(
                            Event.RANDOMGAME,
                            new GameInfoRequest(
                                    new PlayGameInfo(takeCardImage.getValue(),
                                            GameEvent.TakeOutCard,
                                            roomId, userId)
                            )),
                    RandomMatchRequestMessage.class,
                    Event.RANDOMGAME);

            // 本地比较渲染
            int i = cardImages.indexOf(takeCardImage);
            cardImages.remove(takeCardImage);
            //  Removes the element at the specified position in this list
            takeOutCards.add(useCards.remove(i));
            takeOutCarsImages.add(takeCardImage);
            takeCardImage.setFlag(2);
            // 更新缓存信息
            GameInfoCard gameInfoCard = CacheUtil.getGameUserInfo().getGameInfoCard();

            gameInfoCard.setUseCards(useCards);
            gameInfoCard.setCardImages(cardImages);
            gameInfoCard.setTakeOutCards(takeOutCards);
            gameInfoCard.setTakeOutCarsImages(takeOutCarsImages);
        }
    }
}
