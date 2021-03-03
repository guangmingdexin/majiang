package com.guang.majiang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardStatus {

    // 牌还未生成 初始状态
    STORAGE(-1),

    // 牌在玩家手中
    HOLD(0),

    // 选中
    READY(1),

    // 出牌
    OUT(2),

    // 牌在桌面上
    USE(3);

    private int value;


}
