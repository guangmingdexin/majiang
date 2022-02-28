package ds.guang.majiang.server.network.codec;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author guangyong.deng
 * @date 2022-02-28 11:25
 */
public class WebSocketProtocol implements Protocol {


    @Override
    public boolean isSelectedProtocol(String protocol) {
        return false;
    }

    @Override
    public ChannelHandlerContext context() {
        return null;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
