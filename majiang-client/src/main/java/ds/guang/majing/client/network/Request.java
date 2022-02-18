package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.network.idle.WorkState;
import ds.guang.majing.client.remote.dto.vo.LoginVo;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.util.DsConstant;
import ds.guang.majing.common.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Objects;

import static ds.guang.majing.common.util.DsConstant.BASE_URL;

/**
 *
 * 封装的 http 请求
 *
 * @author guangmingdexin
 */
@Getter
@Setter
@Accessors(chain = true)
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
    protected static RequestConfig config;

    protected static HttpClientContext ctx;

    /**
     * 超时等待时间 5 分钟
     */
    protected static int waitTime;


    protected String url;

    /**
     * 底层 socket 对象
     */
    protected Socket socket;

    static {

        waitTime = 30 * 60 * 1000;
        config = RequestConfig.custom()
                .setSocketTimeout(waitTime)
                .setConnectTimeout(waitTime)
                .build();

        httpClient = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();

        ctx = HttpClientContext.create();

    }

    public Request(Object message) {
        this(message, BASE_URL);
    }

    public Request(Object message, String url) {
        // 默认请求
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        // 直接将 data 封装为 message，或者直接传一个

        // 创建Post请求
        this.message = message;
        this.url = url;
        this.httpPost = new HttpPost(url);

        try {
            this.getHttpPost().setEntity(new StringEntity(JsonUtil.objToJson(message)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setAuthorizeHeader();

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
     * @return 返回数据
     */
    private String call() {

        System.out.println("发起请求的线程-" + Thread.currentThread().getName() + " service: " + url);
        // 1.向远程服务器发送准备游戏的请求
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        String reply = null;

        // 由客户端执行(发送)Post请求
        try {
            reply =  httpClient.execute(httpPost, res -> {
                // 从响应模型中获取响应实体
                HttpEntity responseEntity = res.getEntity();
                // 更新读时间
                // WorkState.idleHandler.
                ManagedHttpClientConnection connection = ctx.getConnection(ManagedHttpClientConnection.class);
                // Can be null if the response encloses no content
                if(null != connection) {
                    Socket socket = connection.getSocket();
                    if(socket != null) {
                        this.socket = socket;
                        System.out.println("获取连接：" + socket + " closed: " + socket.isClosed());
                    }else {
                        throw new NullPointerException("连接为空！");
                    }
                }
                if (responseEntity != null) {
                    return EntityUtils.toString(responseEntity);
                }
                return null;
            }, ctx);

            Objects.requireNonNull(reply, "reply is null");

            System.out.println("reply: " + reply + " closed: " + socket.isClosed());

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


    protected void setAuthorizeHeader() {

        this.httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        this.httpPost.setHeader("Connection", "keep-alive");
        // 获取缓存的 token
        LoginVo token = CacheUtil.getToken();

        if(token != null) {
            this.httpPost.setHeader("UID", token.getUid());
            this.httpPost.setHeader("TOKEN", token.getToken());
        }
    }
}
