package com.guang.majiangclient.client.message;

import com.guang.majiangclient.client.common.GenericMessage;
import com.guang.majiangclient.client.common.annotation.ClassInfo;
import com.guang.majiangclient.client.common.annotation.Package;
import com.guang.majiangclient.client.common.enums.MessageType;
import com.guang.majiangclient.client.common.enums.MessageVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName Ping
 * @Author guangmingdexin
 * @Date 2021/6/16 14:34
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
@Package(version = MessageVersion.V10, type = MessageType.Ping)
public class PingRequestMessage extends GenericMessage {

    @ClassInfo(isDeserializer = true)
    private String info;

    public PingRequestMessage(String info) {
        this.info = info;
    }
}
