package ds.guang.majing.client.context;

import ds.guang.majing.client.event.DsRequest;
import ds.guang.majing.client.event.DsResponse;

/**
 * 全局变量管理器
 */
public class DefaultContext implements Context {

    @Override
    public DsRequest getRequest() {
        return null;
    }

    @Override
    public DsResponse getResponse() {
        return null;
    }
}
