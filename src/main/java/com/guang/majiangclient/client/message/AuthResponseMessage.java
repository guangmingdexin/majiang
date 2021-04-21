package com.guang.majiangclient.client.message;

import com.guang.majiangclient.client.common.*;
import com.guang.majiangclient.client.common.Package;
import com.guang.majiangclient.client.entity.AuthResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName AuthResponseMessage
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/8 14:35
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
@Package(version = MessageVersion.V10, type = MessageType.Response)
public class AuthResponseMessage extends GenericMessage {

    @ClassInfo(isDeserializer = true)
    private AuthResponse response;

    public AuthResponseMessage(AuthResponse response) {
        this.response = response;
    }

}
