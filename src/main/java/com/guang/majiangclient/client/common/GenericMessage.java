package com.guang.majiangclient.client.common;

import com.guang.majiangclient.client.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName GenericMessage
 * @Description 消息
 * @Author guangmingdexin
 * @Date 2021/4/1 9:34
 * @Version 1.0
 **/
@Getter
@Setter
public abstract class GenericMessage implements Message {

    // 数据包 开始分隔符
    public final static byte[] PKG_PREFIX = new byte[]{(byte)0xFF, (byte)0xFE, 0x06, 0x08};

    // 数据包 结尾分隔符
    public final static byte[] PKG_SUFFIX = new byte[]{(byte)0xFF, (byte)0xFE, 0x06, 0x08};

    // 数据最大长度
    public static final int PKG_MAX_LENGTH = Integer.MAX_VALUE - 16;

    // 数据包总长度
    protected int totalLen;

    // 协议的版本 (如何进行解码)
    protected short version;
    // 消息的类型 （网络连接， 权限认证， ）
    protected short channel;

    // 数据长度
    protected int length;


    @Override
    public String toString() {
        return encoder(this);
    }

    @Override
    public String encoder(Object message) {
        return JsonUtil.objToString(message);
    }

    @Override
    public GenericMessage decoder(String json) {
        return null;
    }


}
