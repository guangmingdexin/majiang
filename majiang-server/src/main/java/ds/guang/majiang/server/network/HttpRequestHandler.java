package ds.guang.majiang.server.network;

import ds.guang.majiang.server.machines.StateMachines;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;


/**
 * @author guangyong.deng
 * @date 2021-12-10 17:36
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> {



    @Override
    protected void channelRead0(ChannelHandlerContext context, HttpObject httpObject) throws Exception {

        // 判断 msg 是否为 http 请求
        if(httpObject instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) httpObject;
            DecoderResult result = request.decoderResult();
            if (!result.isSuccess()) {
                ResponseUtil.response(DsMessage.build("-1", "-1", DsResult.error("无法解码！")));
                return;
            }
            DsMessage message = (DsMessage)HttpRequestParser.getClassContent(request, DsMessage.class);
            System.out.println(message);
            // 根据不同业务调用不同的处理逻辑
            DsResult reply = StateMachines.get(message.getServiceNo()).event(message.getServiceNo(), message);
            System.out.println("reply: " + reply);
            reply = reply == null ?  DsResult.empty() : reply;
            //  构造返回消息
            DsMessage copyMessage = DsMessage.copy(message).setData(reply);
            System.out.println("回传的数据：" + copyMessage);
            // 构造一个 http 的响应 即 httpResponse
            context.writeAndFlush(ResponseUtil.response(copyMessage));
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
