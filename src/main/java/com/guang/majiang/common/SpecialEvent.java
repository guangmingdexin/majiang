package com.guang.majiang.common;

/**
 * @author asus
 */

public enum SpecialEvent {

    PONG("碰"),
    KONG("杠"),
    HU("胡"),
    IGNORE("过");

    private String event;

     SpecialEvent(String event) {
        this.event = event;
    }
}
