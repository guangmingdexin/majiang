package com.guang.majiangclient.client.message;

import com.guang.majiangclient.client.common.*;
import com.guang.majiangclient.client.common.annotation.ClassInfo;
import com.guang.majiangclient.client.common.annotation.Package;
import com.guang.majiangclient.client.common.enums.MessageType;
import com.guang.majiangclient.client.common.enums.MessageVersion;
import com.guang.majiangclient.client.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName AuthMessage
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/1 15:16
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
@Package(version = MessageVersion.V10, type = MessageType.Auth)
public class AuthRequestMessage extends GenericMessage {

    // User : 消息包携带的 bean 对象
    @ClassInfo(isDeserializer = true)
    private User user;

    public AuthRequestMessage(User user) {
        this.user = user;
    }

}
