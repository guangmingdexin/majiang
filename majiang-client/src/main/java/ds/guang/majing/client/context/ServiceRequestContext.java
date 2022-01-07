package ds.guang.majing.client.context;

import ds.guang.majing.client.network.Request;

/**
 * @author guangyong.deng
 * @date 2021-12-24 10:51
 */
public interface ServiceRequestContext<E> {

    Request getRequest(E id);

    boolean putRequest(E id, Request request);

    boolean remove(E id);

    boolean isLimitCount();


    int getLimitCount();

    /**
     *
     * 接口访问的时间不能超过 每 second 秒 count 次
     *
     * @param second
     * @param count
     */
    void setLimitCount(int second, int count);
}
