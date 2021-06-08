package com.guang.majiangclient.client.common.enums;

/**
 * @author guangmingdexin
 */

public enum GameEvent {

    // 启动游戏
    InitialGame(-1),
    // 开始游戏
    StartGame(99),
    // 发牌
    TakeCards(101),
    // 摸牌
    TakeCard(102),
    // 出牌
    TakeOutCard(103),
    // 回合转换标志
    Ack(-99),
    // 碰
    Pong(1 ),
    // 杠
    Gang(1 << 1),
    // 胡
    Hu(1 << 2),
    // 忽滤
    Ignore(-99);

    int state;

    GameEvent(int state) {
        this.state = state;
    }

    public int intValue() {
        return state;
    }

    public static GameEvent value(int state) {
        for (GameEvent value : values()) {
            if(value.intValue() == state) {
                return value;
            }
        }
        return null;
    }

}
