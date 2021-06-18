package com.guang.majiangclient.client.message;

/**
 * @ClassName FriendRequestMessage
 * @Author guangmingdexin
 * @Date 2021/6/17 11:29
 * @Version 1.0
 **/

import com.guang.majiangclient.client.common.GenericMessage;
import com.guang.majiangclient.client.common.annotation.ClassInfo;
import com.guang.majiangclient.client.common.annotation.Package;
import com.guang.majiangclient.client.common.enums.MessageType;
import com.guang.majiangclient.client.common.enums.MessageVersion;
import com.guang.majiangclient.client.entity.Friend;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Package(version = MessageVersion.V10, type = MessageType.Friend)
public class FriendRequestMessage extends GenericMessage {

    @ClassInfo(isDeserializer = true)
    private Friend friend;

    public FriendRequestMessage(Friend friend) {
        this.friend = friend;
    }
}
