package com.guang.majiang.layout;

import com.guang.majiang.image.BackgroundMyImage;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerNode;

import java.util.List;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
public interface Init {

    // 添加背景
    BackgroundMyImage addBackground();

    // 添加棋牌
    List<CardImage> addCards();

    // 初始化玩家手牌
    PlayerNode<Player> addPlayerCard();

    // 条件头像


}
