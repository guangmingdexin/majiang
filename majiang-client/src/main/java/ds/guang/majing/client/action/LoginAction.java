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

        if(!sync) {
            //new Thread(event).start();
        }
        return DsResult.empty();
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
