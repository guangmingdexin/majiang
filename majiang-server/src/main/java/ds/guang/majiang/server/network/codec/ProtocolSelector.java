package ds.guang.majiang.server.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author guangyong.deng
 * @date 2022-02-28 10:26
 */
public class ProtocolSelector extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 1.检查前几个字节长度是否代表协议头（首先预定义长度）

        // 1.1 如果协议头是 #ws，#http 则添加相应协议解析器
        // 1.2 如果不是协议头，则检查 ctx 是否存在协议解析器，并判断协议解析器
            // 是否能够正常解析相应数据，如果没有协议解析器，则抛出异常

        // 2.可以动态的增加协议解析器，并符合开闭原则，（适合的设计模式，策略，简单工厂，单例，）

        // 3.
    }


    /**
     *
     * 检查
     *
     * @return
     */
    boolean checkProtocol() {

        return false;
    }
}
