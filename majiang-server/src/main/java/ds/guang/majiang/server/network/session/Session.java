package ds.guang.majiang.server.network.session;

import ds.guang.majiang.server.network.buffer.MessageBuffer;
import io.netty.channel.Channel;

/**
 * @author guangyong.deng
 * @date 2022-01-10 10:20
 */
public interface Session {


    /**
     *
     * 获取通道
     *
     * @param userId 用户 id
     * @return
     */
    Object getContext(String userId);


    /**
     *
     * 获取缓冲区队列
     *
     * @return
     */
    MessageBuffer getBuffer();


    /**
     *
     * 获取每一次请求的 offset
     *
     * @return
     */
    int getOffset();


}
