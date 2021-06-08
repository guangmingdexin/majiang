package com.guang.majiangclient.client.message;

import com.guang.majiangclient.client.common.*;
import com.guang.majiangclient.client.common.annotation.ClassInfo;
import com.guang.majiangclient.client.common.annotation.Package;
import com.guang.majiangclient.client.common.enums.MessageType;
import com.guang.majiangclient.client.common.enums.MessageVersion;
import com.guang.majiangclient.client.entity.GameInfoRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName RandomMatchRequestMessage
 * @Author guangmingdexin
 * @Date 2021/4/21 15:39
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
@Package(version = MessageVersion.V10, type = MessageType.RandomGame)
public class RandomMatchRequestMessage extends GenericMessage {

    @ClassInfo(isDeserializer = true)
    private GameInfoRequest gameInfo;

    public RandomMatchRequestMessage(GameInfoRequest gameInfo) {
        this.gameInfo = gameInfo;
    }
}
