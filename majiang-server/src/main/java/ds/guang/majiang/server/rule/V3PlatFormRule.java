package ds.guang.majiang.server.rule;

import ds.guang.majiang.server.layer.basic.ActionManager;
import ds.guang.majing.common.factory.DefaultStateStrategyFactory;
import ds.guang.majing.common.factory.StateStrategy;
import ds.guang.majing.common.factory.StateStrategyFactory;
import ds.guang.majing.common.rule.AbstractRule;
import ds.guang.majing.common.rule.Rule;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;

import java.util.function.Supplier;

import static ds.guang.majing.common.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2021-12-23 14:39
 */
@SuppressWarnings("unchecked")
public class V3PlatFormRule extends AbstractRule<String, StateMachine<String, String, Object>> {

    private StateStrategyFactory stateStrategyFactory;

    private StateStrategy stateStrategy;

    public V3PlatFormRule() {
        stateStrategyFactory = DefaultStateStrategyFactory.INSTANCE;
        stateStrategy = stateStrategyFactory.newStateStrategy();
    }

    public V3PlatFormRule(StateStrategyFactory stateStrategyFactory) {
        this.stateStrategyFactory = stateStrategyFactory;
        stateStrategy = stateStrategyFactory.newStateStrategy();
    }

    private Supplier<State<String, String, Object>> loginStateSupplier = () -> stateStrategy.newState(() -> STATE_LOGIN_ID);

    private Supplier<State<String, String, Object>> platformSupplier = () -> stateStrategy.newState(() -> STATE_PLATFORM_ID);

    private Supplier<State<String, String, Object>> prepareSupplier = () -> stateStrategy.newState(() -> STATE_PREPARE_ID);

    @Override
    public Rule<String, StateMachine<String, String, Object>> create(String s) {

        State<String, String, Object> loginState = loginStateSupplier.get();
        State<String, String, Object> platformState = platformSupplier.get();
        State<String, String, Object> prepareState = prepareSupplier.get();

        // 直接注册事件
        ActionManager.onEvent(loginState, platformState, prepareState);

        // 创建状态机
        StateMachine<String, String, Object> ruleActor = getRuleActor();
        ruleActor.registerInitialState(loginState);
        ruleActor.registerState(platformState, prepareState);
        // 开启状态机，必不可少的一步
        ruleActor.start();
        return this;
    }
}
