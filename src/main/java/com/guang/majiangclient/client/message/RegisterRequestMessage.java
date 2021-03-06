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
 * @ClassName RegisterRequestMessage
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/18 8:56
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
@Package(version = MessageVersion.V10, type = MessageType.Register)
public class RegisterRequestMessage extends GenericMessage {

    @ClassInfo(isDeserializer = true)
    private User user;

    public RegisterRequestMessage(User user) {
        this.user = user;
    }
}
