package com.guang.majiangclient.client.common.enums;

/**
 * @author 光明的心
 */

public enum MessageType {

    // 未知的信息类型
    Unknown(0),
    // 用户校验
    Auth(1),
    // 注册
    Register(2),
    // 登陆
    Login(3),
    // 游戏包信息
    RandomGame(4),
    // 响应包信息
    Response(99);

    private short type;

    MessageType(int type) {
        this.type = (short) type;
    }

    public static MessageType valueOf(short type) {
        for (MessageType t : values()) {
            if(t.type == type) {
                return t;
            }
        }
        return Unknown;
    }

    public short getType() {
        return type;
    }
}
