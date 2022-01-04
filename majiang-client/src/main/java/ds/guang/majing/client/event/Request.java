package ds.guang.majing.client.event;

import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 *
 * 封装的 http 请求
 *
 * @author guangmingdexin
 */
public abstract class Request  {

    /**
     * 线程安全的
     */
    protected  CloseableHttpClient httpClient;

    protected  HttpPost httpPost;

    protected Object message;

    protected StringEntity entity;

    protected String header;

    /**
     * 构建超时等配置信息
     */
    RequestConfig config;

    /**
     * 超时等待时间 5 分钟
     */
    protected int waitTime;

    protected String url;


    public Request(Object message) {
        // 默认请求
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        // 直接将 data 封装为 message，或者直接传一个
        this.setWaitTime(30 * 60 * 1000)
                        .setConfig(
                                RequestConfig.custom()
                                    .setSocketTimeout(waitTime)
                                        .setConnectTimeout(waitTime)
                                        .build());

        this.setHttpClient(HttpClientBuilder.create().build())
                // 创建Post请求
                .setUrl("http://192.168.0.5:9001/")
                .setMessage(message)
                .setHttpPost(new HttpPost(url))
                .setConfig(config);

        try {
            this.getHttpPost().setHeader("Content-Type", "application/json;charset=utf8");
            this.getHttpPost().setEntity(new StringEntity(JsonUtil.objToJson(message)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交任务
     */
    protected abstract void before(Runnable task);

    /**
     *
     * 异步执行任务的回调
     *
     * @return dsResult
     */
    protected abstract DsResult after(DsResult result);


    /**
     * 按照流程执行
     */
    public final DsResult execute(Runnable task, Object data) {
        before(task);
        return after(asynHttpPost(data));
    }

    /**
     *
     * 发起 http 请求
     *
     * @return
     */
    public DsResult call() {

        System.out.println("发起请求的线程-" + Thread.currentThread().getName());
        // 1.向远程服务器发送准备游戏的请求
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        DsMessage reply = null;
        DsResult result = null;
        // 由客户端执行(发送)Post请求
        try {
            reply =  httpClient.execute(httpPost, res -> {
                // 从响应模型中获取响应实体
                HttpEntity responseEntity = res.getEntity();
                if (responseEntity != null) {
                    return (DsMessage) JsonUtil.stringToObj(EntityUtils.toString(responseEntity), DsMessage.class);
                }
                return null;
            });
            Objects.requireNonNull(reply, "reply is null");
            result = (DsResult) JsonUtil.mapToObj(reply.getData(), DsResult.class);

        } catch (IOException e) {
            e.printStackTrace();
            return DsResult.error(e.getMessage());
        }
        return result;
    }

    public DsResult asynHttpPost(Object data) {
        return call();
    }


    public Request setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public HttpPost setHttpPost(HttpPost httpPost) {
        this.httpPost = httpPost;
        return this.httpPost;
    }

    public Request setMessage(Object message) {
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

    public Object getMessage() {
        return message;
    }

    public StringEntity getEntity() {
        return entity;
    }

    public String getHeader() {
        return header;
    }

    public RequestConfig getConfig() {
        return config;
    }

    public RequestConfig setConfig(RequestConfig config) {
        this.config = config;
        return this.config;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public Request setWaitTime(long waitTime) {
        this.waitTime = (int) waitTime;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

}
