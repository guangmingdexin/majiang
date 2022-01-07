package ds.guang.majing.client.context;

import ds.guang.majing.client.network.Request;

/**
 * @author guangyong.deng
 * @date 2021-12-24 10:59
 */
public class DefaultServiceRequestContext implements ServiceRequestContext<RequestId> {

    @Override
    public Request getRequest(RequestId id) {
        return null;
    }

    @Override
    public boolean putRequest(RequestId id, Request request) {
        return false;
    }

    @Override
    public boolean remove(RequestId id) {
        return false;
    }

    @Override
    public boolean isLimitCount() {
        return false;
    }

    @Override
    public int getLimitCount() {
        return 0;
    }

    @Override
    public void setLimitCount(int second, int count) {

    }
}
