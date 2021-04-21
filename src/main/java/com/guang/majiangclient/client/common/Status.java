package com.guang.majiangclient.client.common;

/**
 * @author guangmingdexin
 */

public enum Status {

    OK(200),
    FAIL(400);


    private int code;

    Status(int code) {
        this.code = code;
    }
}
