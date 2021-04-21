package com.guang.majiangclient.client.common;

/**
 * @author guangmingdexin
 */
public enum MessageVersion {

    /**
     * 数据包数据类型
     */
    Unknown(0),
    V10(0x0010);

    private short version;

    MessageVersion(int type) {
        this.version = (short) type;
    }

    public static MessageVersion valueOf(short version) {
        for (MessageVersion v : values()) {
            if(v.version == version) {
                return v;
            }
        }
        
        return Unknown;
    }

    public short getVersion() {
        return version;
    }
}
