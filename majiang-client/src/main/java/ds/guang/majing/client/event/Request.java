package ds.guang.majing.client.event;

import ds.guang.majing.client.context.Context;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.cache.DsGlobalCache;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public abstract class Request {

    protected  CloseableHttpClient httpClient;

    protected  HttpPost httpPost;

    protected DsMessage message;

    protected StringEntity entity;

    protected String header;


    public DsResult call() {
        // 1.向远程服务器发送准备游戏的请求
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)

        DsMessage reply = null;
        DsResult result = null;
        CloseableHttpResponse response = null;
        // 由客户端执行(发送)Post请求
        try {
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                reply = (DsMessage) JsonUtil.stringToObj(EntityUtils.toString(responseEntity), DsMessage.class);
                result = (DsResult) JsonUtil.mapToObj(reply.getData(), DsResult.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return DsResult.error(e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public Request setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public Request setHttpPost(HttpPost httpPost) {
        this.httpPost = httpPost;
        return this;
    }

    public Request setMessage(DsMessage message) {
        this.message = message;
        return this;
    }

    public Request setEntity(StringEntity entity) {
        this.entity = entity;
        return this;
    }

    public Request setHeader(String header) {
        this.header = header;
        return this;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public HttpPost getHttpPost() {
        return httpPost;
    }

    public DsMessage getMessage() {
        return message;
    }

    public StringEntity getEntity() {
        return entity;
    }

    public String getHeader() {
        return header;
    }
}
