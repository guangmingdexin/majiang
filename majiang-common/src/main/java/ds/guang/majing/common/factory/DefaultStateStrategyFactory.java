package ds.guang.majing.common.factory;

/**
 * @author guangyong.deng
 * @date 2021-12-23 9:29
 */
public class DefaultStateStrategyFactory implements StateStrategyFactory {

    public static final StateStrategyFactory INSTANCE = new DefaultStateStrategyFactory();

    private DefaultStateStrategyFactory() {}

    @Override
    public StateStrategy newStateStrategy() {
        return DefaultStateStrategy.INSTANCE;
    }
}
