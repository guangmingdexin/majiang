package ds.guang.majiang.server.network.codec;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author guangyong.deng
 * @date 2022-02-28 10:44
 */
public class HttpProtocol implements Protocol {


    /**
     * 协议头
     */
    private String protocolHeader;


    /**
     * 通道
     */
    private ChannelHandlerContext context;


    public HttpProtocol(String protocolHeader, ChannelHandlerContext context) {
        this.protocolHeader = protocolHeader;
        this.context = context;
    }

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
