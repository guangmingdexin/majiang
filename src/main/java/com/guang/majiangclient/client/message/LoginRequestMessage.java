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
 * @ClassName LoginRequestMessage
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/21 14:49
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
@Package(version = MessageVersion.V10, type = MessageType.Login)
public class LoginRequestMessage extends GenericMessage {

    @ClassInfo(isDeserializer = true)
    private User user;

    public LoginRequestMessage(User user) {
        this.user = user;
    }
}
