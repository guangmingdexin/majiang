package ds.guang.majiang.server.network.codec;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author guangyong.deng
 * @date 2022-02-28 10:41
 */
public interface ProtocolFactory {

    /**
     *
     * 根据协议名称，创建不同的协议解析器
     *
     * @param protocol 协议名称
     * @return 协议解析器
     */
    Protocol create(String protocol, ChannelHandlerContext context);


    /**
     *
     * 设置不同的创建方式，为解析器设置不同的生效范围
     * 1. 所有的 context 共用
     * 2. 每一个 context 有一个 解析器（减少每次创建对象的消耗）
     *
     * @param protocol
     * @param context
     * @return
     */
    Protocol get(String protocol, ChannelHandlerContext context);
}
