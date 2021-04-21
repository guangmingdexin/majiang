package com.guang.majiangclient.client.entity;

import com.guang.majiangclient.client.common.Event;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @ClassName AuthResponse
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/8 14:36
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {

    private Date date;

    private Object body;

    private int code;

    private String msg;

    private Event event;

    private boolean result;

    public AuthResponse(Date date, Object body, int code, Event event) {
        this.date = date;
        this.body = body;
        this.code = code;
        this.event = event;
    }

    public void isSuccess() {
        result = true;
    }

    public void isFail() {
        result = false;
    }
}
