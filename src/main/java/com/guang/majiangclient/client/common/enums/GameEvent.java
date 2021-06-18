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
    // 事件确认标志
    AckEvent(-99),
    // 回合转换标志
    AckAround(-98),
    // 碰
    Pong(1 << 1),
    // 巴杠
    Gang1(1 << 2),
    // 直杠
    Gang2(1 << 3),
    // 暗杠
    Gang3(1 << 4),
    // 胡
    Hu(1 << 5),
    // 两家同时 胡牌
    Hu2(1 << 6),
    // 三级同时胡牌
    Hu3(1 << 7),
    // 忽滤
    Ignore(1);

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
