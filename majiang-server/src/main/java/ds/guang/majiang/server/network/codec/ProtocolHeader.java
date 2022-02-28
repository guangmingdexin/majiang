package ds.guang.majiang.server.network.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author guangyong.deng
 * @date 2022-02-28 11:17
 */
@Getter
@AllArgsConstructor
public enum ProtocolHeader {

    /**
     * 定义协议头
     */
    Http("#http"),
    Websocket("#websocket");


    private String protocol;
}
