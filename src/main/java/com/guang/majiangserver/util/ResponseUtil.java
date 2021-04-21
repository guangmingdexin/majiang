package com.guang.majiangserver.util;

import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.message.AuthResponseMessage;

import java.util.Date;

/**
 * @ClassName ResponseUtil
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/20 18:29
 * @Version 1.0
 **/
public final class ResponseUtil {

    public static void responseBuildFactory(AuthResponseMessage response, Object body, int code, Event event,
                                            String msg, boolean res) {
        AuthResponse authResponseMsg = new AuthResponse(new Date(), body, code, event);
        if(res) {
            authResponseMsg.isSuccess();
        }else {
            authResponseMsg.isFail();
        }
        authResponseMsg.setMsg(msg);
        response.setResponse(authResponseMsg);
    }
}
