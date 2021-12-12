package ds.guang.majing.client.event;


import ds.guang.majing.common.DsResult;

import java.util.concurrent.Callable;

/**
 *
 * 事件包括用户事件 以及 系统事件
 *
 * 玩家可触发的事件接口
 *
 * @author guangyong.deng
 * @date 2021-12-08 15:40
 */
public interface Event<T> {

    T call();

}
