package ds.guang.majing.common.protocol;

import lombok.Data;

/**
 * @author guangyong.deng
 * @date 2022-02-28 13:35
 */
@Data
public class SimpleProtocol {

    /**
     * 协议类型
     */
    private String protocol;

    /**
     * 默认消息协议长度
     */
    public final static int PROTOCOL_HEAD_LEN = 10;

    /**
     * 消息体长度
     */
    private int bodyLength;
    /**
     * 消息内容
     */
    private byte[] body;

}
