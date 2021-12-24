package ds.guang.majing.common.factory;

/**
 * @author guangyong.deng
 * @date 2021-12-23 9:22
 */
public interface StateStrategyFactory<T, E, R> {


    /**
     *
     * 返回创建策略
     *
     * @return
     */
    StateStrategy<T, E, R> newStateStrategy();
}
