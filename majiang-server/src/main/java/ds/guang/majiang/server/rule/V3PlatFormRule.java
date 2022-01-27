package ds.guang.majiang.server.rule;

import ds.guang.majiang.server.layer.basic.ActionManager;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.factory.DefaultStateStrategyFactory;
import ds.guang.majing.common.factory.StateStrategy;
import ds.guang.majing.common.factory.StateStrategyFactory;
import ds.guang.majing.common.game.rule.AbstractRule;
import ds.guang.majing.common.game.rule.Rule;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;

import java.util.function.Supplier;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2021-12-23 14:39
 */
@SuppressWarnings("unchecked")
public class V3PlatFormRule extends AbstractRule<String, StateMachine<String, String, DsResult>> {

    private final StateStrategyFactory stateStrategyFactory;

    private StateStrategy stateStrategy;

    public V3PlatFormRule() {
        stateStrategyFactory = DefaultStateStrategyFactory.INSTANCE;
        stateStrategy = stateStrategyFactory.newStateStrategy();
    }

    public V3PlatFormRule(StateStrategyFactory stateStrategyFactory) {
        this.stateStrategyFactory = stateStrategyFactory;
        stateStrategy = stateStrategyFactory.newStateStrategy();
    }

    private Supplier<State<String, String, DsResult>> loginStateSupplier = () -> stateStrategy.newState(() -> STATE_LOGIN_ID);

    private Supplier<State<String, String, DsResult>> platformSupplier = () -> stateStrategy.newState(() -> STATE_PLATFORM_ID);

    private Supplier<State<String, String, DsResult>> prepareSupplier = () -> stateStrategy.newState(() -> STATE_PREPARE_ID);

    /**
     * 游戏状态 - 摸牌， 出牌，事件
     */
    private Supplier<State<String, String, DsResult>> gameTakeCardStateSupplier = () -> stateStrategy.newState(() -> STATE_TAKE_CARD_ID);

    private Supplier<State<String, String, DsResult>> gameTakeOutCardStateSupplier = () -> stateStrategy.newState(() -> STATE_TAKE_OUT_CARD_ID);

    private Supplier<State<String, String, DsResult>> gameEventStateSupplier = () -> stateStrategy.newState(() -> STATE_EVENT_ID);

    private Supplier<State<String, String, DsResult>> gameWaitStateSupplier = () -> stateStrategy.newState(() -> STATE_WAIT_ID);

    @Override
    public Rule<String, StateMachine<String, String, DsResult>> create(String s) {

        State<String, String, DsResult> loginState = loginStateSupplier.get();
        State<String, String, DsResult> platformState = platformSupplier.get();
        State<String, String, DsResult> prepareState = prepareSupplier.get();


        State<String, String, DsResult> takeCardState = gameTakeCardStateSupplier.get();
        State<String, String, DsResult> takeOutCardState = gameTakeOutCardStateSupplier.get();
        State<String, String, DsResult> eventState = gameEventStateSupplier.get();
        State<String, String, DsResult> waitState = gameWaitStateSupplier.get();

        // 直接注册事件
        ActionManager.onEvent(loginState, platformState, prepareState,  takeCardState, takeOutCardState, eventState, waitState);

        // 创建状态机
        StateMachine<String, String, DsResult> ruleActor = getRuleActor();
        ruleActor.registerInitialState(loginState);
        ruleActor.registerState(platformState, prepareState, takeCardState, takeOutCardState, eventState, waitState);
        // 开启状态机，必不可少的一步
        ruleActor.start();
        return this;
    }
}
