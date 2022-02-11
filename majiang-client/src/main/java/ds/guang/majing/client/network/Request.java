package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.util.DsConstant;
import ds.guang.majing.common.util.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
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
    protected static CloseableHttpClient httpClient;

    protected HttpPost httpPost;

    protected Object message;

    protected StringEntity entity;

    protected String header;

    /**
     * 构建超时等配置信息
     */
    protected static  RequestConfig config;

    /**
     * 超时等待时间 5 分钟
     */
    protected static int waitTime;


    protected String url;

    static {

        httpClient = HttpClientBuilder.create().build();
        waitTime = 30 * 60 * 1000;
        config = RequestConfig.custom()
                .setSocketTimeout(waitTime)
                .setConnectTimeout(waitTime)
                .build();



    }

    public Request() {
    }

    public Request(Object message) {
        this.message = message;
        this.url = DsConstant.BASE_URL;
        init();
    }

    public Request(Object message, String url) {
        // 默认请求
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        // 直接将 data 封装为 message，或者直接传一个

        // 创建Post请求
        this.message = message;
        this.url = DsConstant.BASE_URL + url;
        init();
    }

    private void init() {

        this.setMessage(message)
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
    protected abstract DsResult after(String content);


    /**
     * 按照流程执行
     */
    public final DsResult execute(Runnable task) {
        before(task);
        String result = call();
        return after(result);
    }

    /**
     *
     * 发起 http 请求
     *
     * @return
     */
    public String call() {

        System.out.println("发起请求的线程-" + Thread.currentThread().getName() + " service: " + ((DsMessage)message).getServiceNo());
        // 1.向远程服务器发送准备游戏的请求
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        String reply = null;

        // 由客户端执行(发送)Post请求
        try {
            reply =  httpClient.execute(httpPost, res -> {
                // 从响应模型中获取响应实体
                HttpEntity responseEntity = res.getEntity();
                if (responseEntity != null) {
                    return EntityUtils.toString(responseEntity);
                }
                return null;
            });
            Objects.requireNonNull(reply, "reply is null");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply;
    }


    protected DsResult response(String content) {

        DsMessage<DsResult<GameInfoResponse>> message = null;

        try {
            message = JsonUtil.getMapper().readValue(content, new TypeReference<DsMessage<DsResult<GameInfoResponse>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(message == null) {
            // TODO : 这里应该抛异常
            return DsResult.error("error！");
        }
        return message.getData();
    }



    public Request setHttpClient(CloseableHttpClient httpClient) {
        Request.httpClient = httpClient;
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
        Request.config = config;
        return Request.config;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public Request setWaitTime(long waitTime) {
        Request.waitTime = (int) waitTime;
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
