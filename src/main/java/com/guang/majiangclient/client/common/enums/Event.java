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
    EXCEPTION("EXCEPTION"),
    DEFAULTEVENT("DEFAULTEVENT");

    private String ui;


    private Event(String ui) {
        this.ui = ui;
    }

    public String getUi() {
        return ui;
    }
}
