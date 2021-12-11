package ds.guang.majing.client.action;



import ds.guang.majing.client.event.Event;
import ds.guang.majing.client.event.LoginEvent;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.dto.User;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author guangyong.deng
 * @date 2021-12-08 16:22
 */
public class LoginAction implements Action<Event, DsResult> {

    /**
     *
     */
    private boolean sync;

    public LoginAction() {
        sync = false;
    }

    public LoginAction(boolean sync) {
        this.sync = true;
    }

    @Override
    public DsResult action(Event event) {


        // 事件类型是否需要使用一个类来表示
        if(event instanceof LoginEvent) {
            System.out.println("调用远程登录服务！");
            LoginEvent loginEvent = (LoginEvent) event;
            User user = loginEvent.data();

            // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            // 创建Post请求
            HttpPost httpPost = new HttpPost("http://localhost:9001/");
            StringEntity entity = new StringEntity(JsonUtil.objToJson(user), "UTF-8");
            // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json;charset=utf8");

            // 响应模型
            try(
                    // 由客户端执行(发送)Post请求
                    CloseableHttpResponse response = httpClient.execute(httpPost)
            ) {
                // 从响应模型中获取响应实体
                HttpEntity responseEntity = response.getEntity();
                System.out.println("响应状态为:" + response.getStatusLine());
                if (responseEntity != null) {
                    System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                    System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // 释放资源
                    if (httpClient != null) {
                        httpClient.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 这里可以继续处理，如果成功
        // if(success)
        System.out.println("登录成功！");
        // if(fail)

        // 问题如何反馈到 面板上：比如登录成功，自动跳转到游戏界面
        // 一：订阅关系 ：引入类似于消息队列机制 将关心这个事件的所有事件源订阅这个动作，结束后，进行群发
        // 二：生产者-消费者：放入后台一个事件回调队列 开启一个新线程不断处理（这个方案较为简单，但不无法进行比较复杂的动作处理）
        //      比如 得到 token 需要将 token 放入缓存中
        //      方案一和二其实本质就是主动权问题，是由调用者处理，还是被调用者处理
        // 三：能不能让 事件源主动感知到事件结束后的结果
        return DsResult.data("token-123344");
    }


    public void run() {
        // 1.第一种方案，每个 action 维持一个 event 队列
            //  每个 action 就是维护一个单个线程的线程池
            //  将业务代码封装到 event 中，再由 action 执行
            // 最后返回结果
            // 一：action 如何管理
            // 二：关于 event 与 action 如何相关联起来
            // 三：线程安全性问题如何解决
            // 四：是否有仿照的框架
    }
}
