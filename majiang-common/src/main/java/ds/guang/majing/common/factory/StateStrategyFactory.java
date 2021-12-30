package ds.guang.majing.common.factory;

import ds.guang.majing.common.state.Result;

/**
 * @author guangyong.deng
 * @date 2021-12-23 9:22
 */
public interface StateStrategyFactory<T, E, R extends Result> {


    /**
     *
     * 返回创建策略
     *
     * @return
     */
    StateStrategy<T, E, R> newStateStrategy();
}
