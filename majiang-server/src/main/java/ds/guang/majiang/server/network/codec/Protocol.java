package ds.guang.majiang.server.network.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

/**
 * @author guangyong.deng
 * @date 2022-02-28 10:42
 */
public interface Protocol extends ChannelHandler {


    /**
     *
     *  判断当前 context 的协议解析器是否能够解析当前协议
     *
     * @param protocol 协议头
     * @return
     */
    boolean isSelectedProtocol(String protocol);


    /**
     *
     * 获取当前 context 的 协议解析器
     *
     * @return
     */
    default Protocol protocol() {
        return context().pipeline().get(Protocol.class);
    }

    /**
     * 获取关联的通道
     *
     * @return context
     */
    ChannelHandlerContext context();
}
