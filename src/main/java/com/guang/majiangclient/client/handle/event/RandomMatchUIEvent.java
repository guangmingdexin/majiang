package com.guang.majiangclient.client.handle.event;


import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.entity.*;
import com.guang.majiangclient.client.handle.log.GameLog;
import com.guang.majiangclient.client.handle.task.Task;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import com.guang.majiangclient.client.handle.service.ServiceCenter;
import com.guang.majiangclient.client.util.ConfigOperation;
import com.guang.majiangclient.client.util.ImageUtil;
import com.guang.majiangclient.client.util.JsonUtil;
import javafx.scene.image.ImageView;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @ClassName RandomMatchUIEvent
 * @Author guangmingdexin
 * @Date 2021/5/21 10:46
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
public class RandomMatchUIEvent implements Runnable {

    private AuthResponse response;

    public RandomMatchUIEvent(AuthResponse response) {
        this.response = response;
    }

    @Override
    public void run() {

       Room room = (Room) JsonUtil.mapToObj((Map<String, Object>) response.getBody(), Room.class);
        boolean result = response.isResult();

        if(result) {
            // 获取游戏阶段
            GameEvent event = room.getGameEvent();
            ServiceCenter center = ServiceCenter.getInstance();
            long roomId = room.getRoomId();
            if(event == GameEvent.InitialGame) {
                // 将 Ui 更新好，进入下一个阶段
                // 当前玩家信息更新
                HashSet<GameUser> players = room.getPlayers();
                for (GameUser player : players) {
                    if(player.getUserId() == CacheUtil.getUserInfo().getUserId()) {
                        CacheUtil.addGameUser(player);
                    }
                }
                center.submit(new AvatarBuildEvent(room));
                center.submit(
                        new Task<>(
                                Event.RANDOMGAME,
                                new GameInfoRequest(
                                        CacheUtil.getGameUserInfo(),
                                        new PlayGameInfo(GameEvent.StartGame,
                                                roomId))),
                        RandomMatchRequestMessage.class,
                        Event.RANDOMGAME);
            }else if(event == GameEvent.StartGame) {
                // 加载本地图片
                List<Integer> numCards = room.getNumCards();
                // 获取图片加载路径
                String path = ConfigOperation.config.get("images").toString();
                List<String> strs = ConfigOperation.numToStrs(numCards);
                try {
                    File dir = Resources.getResourceAsFile(path);
                    List<ImageView> load = ImageUtil.load(dir, strs);
                    // 更新玩家信息
                    if(load.size() != numCards.size()) {
                        throw new IllegalArgumentException("图片加载错误！");
                    }

                    HashSet<GameUser> players = room.getPlayers();

                    Direction cur = null;
                    HashMap<Direction, GameInfoCard> infoCards = new HashMap<>(4);
                    // 设置当前回合
                    CacheUtil.setCurDire(room.getAround());
                    for (GameUser player : players) {
                        GameInfoCard gameInfoCard = player.getGameInfoCard();
                        // 在使用的时候才进行初始化
                        // 出牌的时候进行初始化
                        List<CardImage> takeOutCarsImages = gameInfoCard.getTakeOutCarsImages();
                        if(takeOutCarsImages == null) {
                            takeOutCarsImages = new ArrayList<>();
                            gameInfoCard.setTakeOutCarsImages(takeOutCarsImages);
                        }
                        if(player.getUserId() == CacheUtil.getUserInfo().getUserId()) {
                            gameInfoCard.setUseCards(numCards);
                            gameInfoCard.buildCard(numCards, load, roomId, player.getUserId());
                            cur = player.getDirection();
                            CacheUtil.addGameUser(player);
                        }else {
                            gameInfoCard.buildCardDown();
                        }
                        infoCards.put(player.getDirection(), gameInfoCard);
                        CacheUtil.addCacheGameUser(player.getDirection(), player);
                    }
                    center.submit(new CardBuildEvent(cur, infoCards));
                    // 手牌初始化

                    GameLog.offer(new PlayGameInfo(
                            roomId,
                            CacheUtil.getUserInfo().getUserId(),
                            -1,
                            cur,
                            GameEvent.InitialGame,
                            0,
                            0,
                            false
                            ));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("收到消息！" + numCards);
            }

        }else {
            System.out.println("消息发送失败！");
        }
        
    }
}
