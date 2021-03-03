package com.guang.majiang.event;

import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.PlayerCard;
import javafx.event.EventHandler;


import java.util.List;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
public interface CardEvent {

    /**
     * 开启事件
     */
    void start();

    /**
     * 关闭事件
     */
    void shutdown();



    /**
     * 为玩家添加手牌
     *
     * @param id 玩家 id
     */
    void addCardHand(String id);

    /**
     * 为手牌注册事件
     *
     * @param handler
     */
    void register(EventHandler handler);
}
