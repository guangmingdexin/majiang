package ds.guang.majing.client.event;


import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.cache.DsGlobalCache;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.dto.User;
import ds.guang.majing.common.event.Event;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author guangyong.deng
 * @date 2021-12-10 15:26
 */
public class LoginRequest extends Request {

    private User user;

    private static final String id = DsConstant.EVENT_LOGIN_ID;

    public LoginRequest(User user) {
        this.user = user;
// 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        this.setHttpClient(HttpClientBuilder.create().build())
                // 创建Post请求
                .setHttpPost(new HttpPost("http://localhost:9001/"))
                // 创建报文
                .setMessage(DsMessage.build(id, "1", user))
                // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
                .setEntity(new StringEntity(JsonUtil.objToJson(message), "UTF-8"))
                .getHttpPost()
                .setEntity(getEntity());

        this.getHttpPost().setHeader("Content-Type", "application/json;charset=utf8");

    }

    @Override
    public DsResult call()  {

        DsResult result = super.call();

        if(result != null && result.isOk()) {
            // 这里应该还需要一个上下文解析器，用来保存用户基本信息和游戏信息
            GameUser data = (GameUser)JsonUtil.mapToObj(result.getData(), GameUser.class);
            DsGlobalCache.getInstance().setObject(data.getUsername(), data, -1);
            System.out.println("登陆成功！");
            return DsResult.data("token-123344");
        }


    // 问题如何反馈到 面板上：比如登录成功，自动跳转到游戏界面
    // 一：订阅关系 ：引入类似于消息队列机制 将关心这个事件的所有事件源订阅这个动作，结束后，进行群发
    // 二：生产者-消费者：放入后台一个事件回调队列 开启一个新线程不断处理（这个方案较为简单，但不无法进行比较复杂的动作处理）
    //      比如 得到 token 需要将 token 放入缓存中
    //      方案一和二其实本质就是主动权问题，是由调用者处理，还是被调用者处理
    // 三：能不能让 事件源主动感知到事件结束后的结果
        return DsResult.error();
    }
}
