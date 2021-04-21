package com.guang.majiang.player;

import com.guang.majiang.common.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 作为一个链表节点类，双向节点
 *
 * @ClassName Player
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/13 20:03
 * @Version 1.0
 **/
@Getter
@Setter
public class Player {

    private String id;

    private String name;

    // 东西南北
    private Direction direction;

    // 是否为庄家
    private boolean bookmaker;

    private volatile int isRound;

    // 游戏状态 0 - 初始化  1 - 进行中  2 - 结束
    private int gameState;

    private PlayerCard playerCard;

    public Player(String id, String name, Direction direction,
                  boolean bookmaker, int isRound, PlayerCard playerCard) {
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.bookmaker = bookmaker;
        this.isRound = isRound;
        this.playerCard = playerCard;
    }


    @Override
    public String toString() {
        return name;
    }
}
