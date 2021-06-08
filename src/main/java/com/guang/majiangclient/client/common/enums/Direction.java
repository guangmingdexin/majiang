package com.guang.majiangclient.client.common.enums;

/**
 * @author guangmingdexin
 */

public enum Direction {

    /**
     * 方位
     */
    ABOVE(0),
    LEFT(1),
    UNDER(2),
    RIGHT(3);

    int direction;

    Direction(int dire) {
        direction = dire;
    }

    public static Direction valueOf(int direction) {
        for (Direction value : values()) {
            if(value.direction == direction) {
                return value;
            }
        }
        System.out.println("有异常...");
        throw new NullPointerException("没有这个方位！");
    }

    public int getDirection() {
        return direction;
    }
}
