package ds.guang.majing.client.action;

import java.util.concurrent.Callable;

/**
 *
 * 预定义的玩家动作接口，一旦玩家触发，必定有个回调动作
 * 同时既然动作应该拥有状态 包括 触发-完成-错误
 * 可以支持同步或者异步
 *
 * @author guangyong.deng
 * @date 2021-12-08 15:30
 */
public interface Action<T, R> {

    /**
     *  传入事件类型，回调相应的动作
     *
     * @param t
     * @return
     */
    R action(T t);
}
