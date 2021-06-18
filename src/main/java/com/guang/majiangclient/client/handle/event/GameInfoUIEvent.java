package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.entity.*;
import com.guang.majiangclient.client.handle.log.GameLog;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.util.ConfigOperation;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GameInfoUIEvent
 * @Author guangmingdexin
 * @Date 2021/6/2 9:47
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
public class GameInfoUIEvent implements Runnable {

    private PlayGameInfo info;

    private boolean isResult;

    public GameInfoUIEvent(PlayGameInfo info, boolean isResult) {
        this.info = info;
        this.isResult = isResult;
    }

    @SneakyThrows
    @Override
    public void run() {
        // TODO 根据不同的事件做出不同的渲染动作
        // 1. 如果是出牌事件，首先从用户缓存中找到该用户信息，将牌渲染到桌面上
        // 2. 出牌过后，同时其他玩家判断是否有特殊事件出牌（碰，杠，胡）
        // 3. 如果没有其他特殊事件，玩家回合到下一个玩家，进行（摸牌-出牌）
        if(isResult) {
            GameEvent event = info.getEvent();
            Direction takeOutDir = info.getDirection();
            int value = info.getValue();
            // 获取出牌的 gameUser
            GameUser user = CacheUtil.getCacheGameUser(takeOutDir);
            Service center = ConfigOperation.getCenter();
            if(event == GameEvent.TakeOutCard) {
                // TODO 玩家统一处理逻辑
                // 1. 界面渲染
                // 2. 逻辑处理
                Direction cur = CacheUtil.getGameUserInfo().getDirection();
                int split = cur.getDirection() - takeOutDir.getDirection();
                GameInfoCard gameInfoCard = user.getGameInfoCard();
                List<CardImage> takeOutCarsImages = gameInfoCard.getTakeOutCarsImages();
                int res = 0;
                if(split == 0) {
                    System.out.println("caCheUtil: " + CacheUtil.getGameUserInfo());
                    System.out.println("lru: " + CacheUtil.getGameUsers());
                    // 获取出的牌
                    CardImage cardImage = takeOutCarsImages.get(takeOutCarsImages.size() - 1);
                    ClientLayout.gBottomGd.getChildren().remove(cardImage.getCard());
                    // 如果是自己的客户端 则 takeOutCarsImage 的 size 从 1 开始
                    // 而 其他客户端 则 takeOutCarsImage 的 size 从 0 开始
                    // 所以 GridPane 中 （0， 1） 坐标无法使用
                    int row = (takeOutCarsImages.size() - 1) / 9;
                    int col = (takeOutCarsImages.size() - 1) % 9;
                    System.out.println("col-row: " + col + "-" + row);
                    ClientLayout.oBottom.add(cardImage.getCard(), col, row);
                }else {
                    System.out.println("caCheUtil: " + CacheUtil.getGameUserInfo());
                    System.out.println("lru: " + CacheUtil.getGameUsers());
                    List<Integer> takeOutCards = gameInfoCard.getTakeOutCards();
                    takeOutCards.add(value);

                    // 加载图片
                    CardImage cardImage = new CardImage();
                    takeOutCarsImages.add(cardImage);
                    int row = takeOutCarsImages.size() / 9;
                    int col = takeOutCarsImages.size() % 9;

                    if(split == 1 || split == -3) {
                        ImageView cardLeft = cardImage.buildLeft(value, 2).getCardLeft();
                        double fitWidth = cardLeft.getFitWidth();
                        double fitHeight = cardLeft.getFitHeight();

                        double x = fitWidth * (3 - (takeOutCarsImages.size() - 1) / 9);
                        double y = fitHeight * ((takeOutCarsImages.size() - 1) % 9);

                        ClientLayout.oLeft.getChildren().add(cardLeft);
                        AnchorPane.setLeftAnchor(cardLeft, 70d + x);
                        AnchorPane.setTopAnchor(cardLeft, y);

                    }else if(split == -1) {
                        ImageView cardRight = cardImage.buildRight(value, 2).getCardRight();
                        double fitWidth = cardRight.getFitWidth();
                        double fitHeight = cardRight.getFitHeight();

                        double x = fitWidth * (3 - (takeOutCarsImages.size() - 1) / 9);
                        double y = fitHeight * ((takeOutCarsImages.size() - 1) % 9);

                        ClientLayout.oRight.getChildren().add(cardRight);
                        AnchorPane.setRightAnchor(cardRight, 70d + x);
                        AnchorPane.setTopAnchor(cardRight, y);

                    }else if(split == 2 || split == -2) {
                        ClientLayout.oTop.add(
                                cardImage.buildTop(value, 2).getCardTop(),
                                col, row);
                    }
                    // 判断是否有特殊事件
                    GameUser cacheGameUser = CacheUtil.getCacheGameUser(cur);
                    CacheUtil.addGameUser(cacheGameUser);

                    if(info.getRes() != 0) {
                        // 有特殊事件发生
                        CacheUtil.setSpecialEvent(true);
                        System.out.println("specialEvent：" + CacheUtil.getSpecialEvent());
                        center.submit(new SpecialOperEvent(info));
                        return;
                    }

                    System.out.println("info: " + this.info);
                }
                // 直接发送确认包
                // 服务器判断是否有特殊事件
                System.out.println("没有特殊事件，请求进行回合切换！");
                center.submit(
                        new Task<>(
                                Event.RANDOMGAME,
                                new GameInfoRequest(
                                        new PlayGameInfo(
                                                info.getRoomId(),
                                                CacheUtil.getUserInfo().getUserId(),
                                                value,
                                                cur,
                                                GameEvent.AckEvent,
                                                0,
                                                0,
                                                false
                                        ))
                        ),
                        RandomMatchRequestMessage.class,
                        Event.RANDOMGAME);
                // 记录出牌日志
                GameLog.offer(info);

            }
        }
    }

}
