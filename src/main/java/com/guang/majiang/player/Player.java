package com.guang.majiang.player;

import com.guang.majiang.common.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName Player
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/13 20:03
 * @Version 1.0
 **/
@Getter
@Setter
@AllArgsConstructor
public class Player {

    private String id;

    private String name;

    // 东西南北
    private Direction direction;

    // 是否为庄家
    private boolean bookmaker;

    private volatile int isRound;

    @Override
    public String toString() {
        return name;
    }
}
