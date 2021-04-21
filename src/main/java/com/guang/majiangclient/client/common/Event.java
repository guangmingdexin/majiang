package com.guang.majiangclient.client.common;

/**
 * @author asus
 */

public enum Event {

    UIEVENT("UI"),
    LOGIN("LOGIN"),
    REGISTER("REGISTER"),
    BUSINESS("BUSINESS"),
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
