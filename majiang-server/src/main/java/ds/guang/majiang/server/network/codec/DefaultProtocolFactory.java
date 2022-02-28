package ds.guang.majiang.server.network.codec;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guangyong.deng
 * @date 2022-02-28 11:03
 */
public class DefaultProtocolFactory implements ProtocolFactory {

    static final Map<String, Protocol> singleProtocols = new ConcurrentHashMap<>();

    @Override
    public Protocol create(String protocol, ChannelHandlerContext context) {

        if(protocol.equals(ProtocolHeader.Http.getProtocol())) {
            return new HttpProtocol(protocol, context);
        }else if(protocol.equals(ProtocolHeader.Websocket.getProtocol())) {
            return new WebSocketProtocol();
        }
        return null;
    }

    @Override
    public Protocol get(String protocol, ChannelHandlerContext context) {

        // 生成 key:
        String key = protocol + context.hashCode();
        return singleProtocols.getOrDefault(key, create(protocol, context));
    }
}
