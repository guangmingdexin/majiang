package ds.guang.majiang.server.network;

import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static ds.guang.majing.common.util.DsConstant.EVENT_CHART;

/**
 * @author guangyong.deng
 * @date 2022-02-25 15:25
 */
public class HttpChatHandler extends SimpleChannelInboundHandler<DsMessage<GameInfoRequest>> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DsMessage<GameInfoRequest> msg) throws Exception {

        // 读取内容，发送给

        System.out.println("chat-msg: " + msg);

        GameInfoRequest request = msg.getData();

        String friendId = request.getFriendId();

        // 找到 f 所在的玩家通道，判断 f 是否上线，如果 f 在线，将消息发送过去
        // 如果 f 未在线，或者 通道已经关闭，将 消息保存，待 f 上线后发送

        ChannelHandlerContext context = HttpRequestHandler.map.get(friendId);
        System.out.println("map: " + context);

        if(context != null && !context.isRemoved()) {

            // 发送消息
            context.writeAndFlush(DsMessage.build(EVENT_CHART, friendId, DsResult.data(request.getData())));
        }

    }
}
