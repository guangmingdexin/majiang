package ds.guang.majiang.server.network.buffer;

/**
 * @author guangyong.deng
 * @date 2022-01-17 16:55
 */
public interface OverflowHandler {


    /**
     *
     * 当加入缓冲区失败后的处理方式
     *
     * @param buffer
     */
    void handler(MessageBuffer buffer);
}
