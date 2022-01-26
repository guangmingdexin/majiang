package ds.guang.majiang.server.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majiang.server.machines.StateMachines;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.util.JsonUtil;
import ds.guang.majing.common.state.StateMachine;
import ds.guang.majing.common.util.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;

import java.util.Objects;

import static ds.guang.majing.common.util.DsConstant.*;


/**
 * @author guangyong.deng
 * @date 2021-12-10 17:36
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext context, HttpObject httpObject) throws Exception {

        // 判断 msg 是否为 http 请求
        // 后期会将游戏业务与普通业务分类，所以可以添加 uri 做判断
        if(httpObject instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) httpObject;
            DecoderResult result = request.decoderResult();
            if (!result.isSuccess()) {
                ResponseUtil.response(DsMessage.build("-1", "-1", DsResult.error("无法解码！")));
                return;
            }
            String content = HttpRequestParser.getContent(request);
            System.out.println("content: " + content);

            // 不忽略大小写
            DsMessage message = JsonUtil.getMapper().readValue(content, new TypeReference<DsMessage<GameInfoRequest>>() {});

            Objects.requireNonNull(message, "message is null");
            Objects.requireNonNull(message.getRequestNo(), "requestNo is null");

            // 根据不同业务调用不同的处理逻辑
            StateMachine<String, String, DsResult> machine = StateMachines
                    .get(preUserMachinekey(message.getRequestNo()));

            // 执行事件
            machine.event(message.getServiceNo(),
                                        // 添加一些自定义的变量进去
                                        message.setAttrMap(SYS_CONTEXT, context));

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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----------------------- 连接 inactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.writeAndFlush(DsMessage.build("-1", "-1", DsResult.error(cause.getMessage())));
    }
}
