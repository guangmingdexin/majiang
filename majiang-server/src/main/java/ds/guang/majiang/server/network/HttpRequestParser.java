package ds.guang.majiang.server.network;

import ds.guang.majing.common.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {


    /**
     * 读取GET方法的参数
     * @param request
     * @return
     */
    public static Map<String, Object> readGetParams(FullHttpRequest request) {

        if (request.method() == HttpMethod.GET) {
            Map<String, Object> paramMap = new HashMap<>();
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            decoder.parameters().forEach((key, value) -> paramMap.put(key, value.get(0)));
            return paramMap;
        }

        return new HashMap<>();
    }


    /**
     * 读取POST方法的表单参数
     *
     * @param request
     * @return 返回一个Map
     */
    public static Map<String, Object> readPostParams(FullHttpRequest request) {
        return JsonUtil.stringToMap(getJsonPostContent(request));
    }


    public static String getJsonPostContent(FullHttpRequest request) {
        if (request.method() == HttpMethod.POST) {
            String contentType = request.headers().get("Content-Type").trim().toLowerCase();
            if (contentType.contains("x-www-form-urlencoded")) {
                StringBuilder builder = new StringBuilder();
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
                List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
                // 判断是否为空，为空可能为心跳包
                if(parmList == null) {
                    return null;
                }

                builder.append("{");
                for (InterfaceHttpData parm : parmList) {
                    if (parm.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        MemoryAttribute data = (MemoryAttribute) parm;
                        builder.append("\"")
                                .append(data.getName())
                                .append("\"")
                                .append(":")
                                .append(data.getValue())
                                .append(",");
                    }
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append("}");
                return builder.toString();
            }
            else if(contentType.contains("application/json")){
                ByteBuf content = request.content();
                return content.toString(CharsetUtil.UTF_8);
            }
        }
        return null;
    }

    public static String getContent(FullHttpRequest request) {
        return getJsonPostContent(request);
    }

}
