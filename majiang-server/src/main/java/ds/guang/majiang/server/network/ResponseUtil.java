package ds.guang.majiang.server.network;

import ds.guang.majing.common.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 *
 * 可以进行 http 配置设定
 *
 * @author guangyong.deng
 * @date 2021-12-14 17:35
 */
public final class ResponseUtil {

    private String contentType;

    private String contentLength;

    public static FullHttpResponse response(Object data) {

        // 回复信息给浏览器 [http]
        ByteBuf content = Unpooled.copiedBuffer(JsonUtil.objToJson(data), CharsetUtil.UTF_8);

        // 构造一个 http 的响应 即 httpResponse
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        return response;
    }
}
