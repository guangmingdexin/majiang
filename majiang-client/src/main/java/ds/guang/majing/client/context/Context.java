package ds.guang.majing.client.context;

import ds.guang.majing.client.event.DsRequest;
import ds.guang.majing.client.event.DsResponse;

/**
 * 上下文管理器
 * 1.保存一些全局变量
 * 2.可以获取以前的一些请求，便于复用
 */
public interface Context {

    /**
     * 获取当前请求的 [Request] 对象
     *
     * @return see note
     */
    DsRequest getRequest();

    /**
     * 获取当前请求的 [Response] 对象
     *
     * @return see note
     */
    DsResponse getResponse();
}
