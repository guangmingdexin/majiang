package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.algorithm.Algorithm;
import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.*;
import com.guang.majiangclient.client.handle.click.RegisterTakeOutEvent;
import com.guang.majiangclient.client.handle.log.GameLog;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.AllArgsConstructor;

import java.util.*;

/**
 * @ClassName SpecialBuildEvent
 * @Author guangmingdexin
 * @Date 2021/6/7 17:23
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
@AllArgsConstructor
public class SpecialBuildEvent implements Runnable {

    private AuthResponse response;

    @Override
    public void run() {
        // 点击事件无论成功还是失败，首先需要将按钮界面隐藏
        ClientLayout.pong.setVisible(false);
        ClientLayout.gang.setVisible(false);
        ClientLayout.hu.setVisible(false);
        ClientLayout.ignore.setVisible(false);

        if(response.isResult()) {

            PlayGameInfo o = (PlayGameInfo) JsonUtil.mapToObj((Map<String, Object>) response.getBody(), PlayGameInfo.class);

            // 首先获取事件类型
            GameEvent event = o.getEvent();

            // TODO 使用一个数据结构作为游戏日志全局记录，方面搜索
            // 可以使用链表（一般只会使用最后一个记录）
            int value = o.getValue();

            if(event == GameEvent.TakeCard) {
                if(value != 0) {
                    // 加载资源
                    ImageView takeCard = ImageUtil.load(ConfigOperation.config.get("images").toString(),
                            ConfigOperation.numToStr(value), true);
                    takeCard.setTranslateY(takeCard.getTranslateY() - 20);

                    // 更新本地玩家信息
                    GameUser curGameUser = CacheUtil.getGameUserInfo();
                    GameInfoCard gameInfoCard = curGameUser.getGameInfoCard();

                    List<Integer> useCards = gameInfoCard.getUseCards();
                    int index = Algorithm.sortInsert(useCards, value);
                    // 先将 index - size() - 1 的所有元素删除
                    ObservableList<Node> children = ClientLayout.gBottomGd.getChildren();
                    children.clear();
                    List<CardImage> cardImages = gameInfoCard.getCardImages();
                    CardImage takeCardImage = new CardImage().build(value, 1, takeCard);
                    cardImages.add(index, takeCardImage);
                    // 将新元素添加进去
                    // index = 10
                    for (int i = 0; i < cardImages.size(); i++) {
                        ClientLayout.gBottomGd.add(cardImages.get(i).getCard(), i, 0);

                    }
                    takeCard.setOnMouseClicked(new RegisterTakeOutEvent(
                            takeCardImage,
                            useCards,
                            cardImages,
                            gameInfoCard.getTakeOutCards(),
                            gameInfoCard.getTakeOutCarsImages(),
                            o.getUserId(),
                            o.getRoomId()));
                }
                // 切换玩家回合
                CommonUtil.markerUi(o.getDirection());
                CacheUtil.next(o.getDirection());
            }else {
                GameUser gameUserCur = CacheUtil.getGameUserInfo();
                GameUser gameUserPrev = CacheUtil.getCacheGameUser(CacheUtil.getCurDire());
                // 获取上家的出牌
                List<Integer> prevUseCards = gameUserPrev.getGameInfoCard().getTakeOutCards();
                prevUseCards.remove(prevUseCards.size() - 1);

                List<CardImage> takeOutCarsImages = gameUserPrev.getGameInfoCard().getTakeOutCarsImages();
                CardImage takeOut = takeOutCarsImages.get(takeOutCarsImages.size() - 1);
                takeOutCarsImages.remove(takeOutCarsImages.size() - 1);

                // 校验
                if(value != takeOut.getValue()) {
                    throw new ArithmeticException("log 日志发生错误！");
                }
                if(event == GameEvent.Pong) {
                    CardImage curTakeOut = new CardImage().build(
                            value, 2,
                            ImageUtil.load(
                                    ConfigOperation.config.get("images").toString(),
                                    ConfigOperation.numToStr(value), true));
                    GameInfoCard gameInfoCard = gameUserCur.getGameInfoCard();
                    LinkedHashMap<Integer, Integer> pgNums = gameInfoCard.getPgNums();
                    LinkedHashMap<Integer, CardImage[]> pg = gameInfoCard.getPg();
                    if(pgNums == null || pg == null) {
                        pgNums = new LinkedHashMap<>();
                        pg = new LinkedHashMap<>();
                        gameInfoCard.setPg(pg);
                        gameInfoCard.setPgNums(pgNums);
                    }
                    pgNums.put(value, 3);
                    List<CardImage> useCardImages = gameInfoCard.getCardImages();
                    List<Integer> useCardNums = gameInfoCard.getUseCards();
                    CardImage[] pong = new CardImage[3];
                    pong[0] = curTakeOut;

                    // 渲染到 ui 界面上
                    // 首先定位到棋牌的位置
                    Pane pane = ClientLayout.getPane(o.getDirection(), CacheUtil.getCurDire());
                    // 不同方位的图片对象不一样
                    // 直接重新加载一张图片
                    if(pane == null) {
                        throw new NullPointerException();
                    }

                    for (int i = 1; i <= 2; i++) {
                        int index = Algorithm.binarySearch(useCardNums, value);
                        if(index < 0) {
                            throw new NullPointerException("手牌中不存在这张牌！");
                        }

                        useCardNums.remove(index);
                        CardImage r = useCardImages.remove(index);

                        pong[i] = r;

                        pane.getChildren().remove(r.getCard());
                    }
                    pg.put(value, pong);

                    // 直接加载图片到
                    int col = pgNums.size();

                    for (CardImage cardImage : pong) {
                        ClientLayout.gBottomPg.add(cardImage.getCard(), col ++ , 0);
                    }

                    GameLog.offer(o);
                    CacheUtil.setSpecialEvent(false);
                    CacheUtil.next(o.getDirection());

                    // TODO 1.先将回合切换到当前玩家身上
                    // 2. 发送消息给服务器，直到玩家出牌才能进行回合切换
                    // 3. 等待玩家出牌
                    if(o.getUserId() == CacheUtil.getGameUserInfo().getUserId()) {
                        Service center = ConfigOperation.getCenter();
                        o.setEvent(GameEvent.AckAround);
                        center.submit(
                                new Task<>(
                                        Event.RANDOMGAME,
                                        new GameInfoRequest(o)
                                ),
                                RandomMatchRequestMessage.class,
                                Event.RANDOMGAME
                        );
                    }

                }
            }
        }
    }
}
