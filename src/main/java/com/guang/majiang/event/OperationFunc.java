package com.guang.majiang.event;


import com.guang.majiang.player.PlayerCard;
import javafx.scene.image.ImageView;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
public interface OperationFunc {

    /**
     * 点击向上
     *
     * @param imageView 选中的牌
     */
    void readyOn(ImageView imageView);

    /**
     * 取消向上
     *
     * @param imageView 未选中的牌
     */
    void readyDown(ImageView imageView);


    /**
     * 模拟玩家出牌
     *
     * @param player 玩家
     */
    void playCard(PlayerCard player);

}
