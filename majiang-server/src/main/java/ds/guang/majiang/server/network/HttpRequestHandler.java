package ds.guang.majiang.server.network;

import ds.guang.majing.common.DsMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Map;

/**
 * @author guangyong.deng
 * @date 2021-12-10 17:36
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext context, HttpObject httpObject) throws Exception {
        System.out.println(httpObject);
        // 判断 msg 是否为 http 请求
        if(httpObject instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) httpObject;
            DecoderResult result = request.decoderResult();
            if (!result.isSuccess()) {
                System.out.println("无法解码！");
                return;
            }

            DsMessage message = (DsMessage)HttpRequestParser.getClassContent(request, DsMessage.class);

            System.out.println(message);

            // 回复信息给浏览器 [http]
            ByteBuf content = Unpooled.copiedBuffer("hello 我是服务器", CharsetUtil.UTF_8);

            // 构造一个 http 的响应 即 httpResponse
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            context.writeAndFlush(response);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接上线！");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
       // System.out.println("发起连接！");
    }
}
