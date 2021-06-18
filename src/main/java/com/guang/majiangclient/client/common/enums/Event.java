package com.guang.majiangclient.client.common.enums;

/**
 * @author asus
 */

public enum Event {

    UIEVENT("UI"),
    LOGIN("LOGIN"),
    REGISTER("REGISTER"),
    RANDOMGAME("RANDOMGAME"),
    GAMEINFO("GAMEINFO"),
    // 特殊事件触发标志
    SPCIALEVENT("SPECIALEVENT"),
    DEFAULTEVENT("DEFAULTEVENT"),
    FRIEND("FRIEND"),
    PING("PING");

    private String event;


    private Event(String event) {
        this.event = event;
    }

}
