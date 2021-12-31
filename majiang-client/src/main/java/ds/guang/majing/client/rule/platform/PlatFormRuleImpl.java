package ds.guang.majing.client.rule.platform;

import ds.guang.majing.client.event.*;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.factory.DefaultStateStrategyFactory;
import ds.guang.majing.common.factory.StateStrategy;
import ds.guang.majing.common.factory.StateStrategyFactory;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.dto.User;
import ds.guang.majing.common.room.Room;
import ds.guang.majing.common.rule.AbstractRule;
import ds.guang.majing.common.rule.Rule;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;

import java.util.function.Supplier;

import static ds.guang.majing.common.DsConstant.*;

/**
 * 规定平台规则
 * @author asus
 */
@SuppressWarnings("unchecked")
public class PlatFormRuleImpl extends AbstractRule<String, StateMachine<String, String, DsResult>> {

    private StateStrategyFactory stateStrategyFactory;

    private StateStrategy stateStrategy;

    public PlatFormRuleImpl() {
       stateStrategyFactory = DefaultStateStrategyFactory.INSTANCE;
       stateStrategy = stateStrategyFactory.newStateStrategy();
    }

    public PlatFormRuleImpl(StateStrategyFactory stateStrategyFactory) {
        this.stateStrategyFactory = stateStrategyFactory;
        stateStrategy = stateStrategyFactory.newStateStrategy();
    }

    private Supplier<State<String, String, DsResult>> loginStateSupplier = () -> stateStrategy.newState(() -> STATE_LOGIN_ID);

    private Supplier<State<String, String, DsResult>> platformSupplier = () -> stateStrategy.newState(() -> STATE_PLATFORM_ID);

    private Supplier<State<String, String, DsResult>> prepareSupplier = () -> stateStrategy.newState(() -> STATE_PREPARE_ID);

    @Override
    public Rule<String, StateMachine<String, String, DsResult>> create(String s) {

        State<String, String, DsResult> loginState = loginStateSupplier.get();
        loginState.onEvent(EVENT_LOGIN_ID, STATE_PLATFORM_ID, data -> {
            // 我应该把这里作为一个异步任务
            Request request = new LoginRequest(data);
            DsResult execute = request.execute(null, null);
            return execute;
        });

        State<String, String, DsResult> platformState = platformSupplier.get();
        platformState.onEntry(data -> {
            // 1.获取客户端资源
            return null;
        });
        platformState.onEvent(EVENT_PREPARE_ID, STATE_PREPARE_ID, data -> {
            // 构造一个 LoginRequest 对象
            Request request = new PrepareRequest(data);
            return request.execute(null, null);
        });

        platformState.onEvent(EVENT_MATCH_FRIEND_ID, STATE_MATCH_FRIEND_ID);


        State<String, String, DsResult> prepareState = prepareSupplier.get();
        prepareState.onEvent(EVENT_POST_HANDCARD_ID, data -> {
            // 固定处理：绑定事件默认传入数据为 DsMessage
                        // 传参只要不是对象，默认使用 Map
            Request request = new PostHandCardRequest(data);

            /**
             * TODO 接口的数据权限如何控制，会存在这样一种情况，该玩家利用自身 token 和 其他玩家 id
             *      获取其他 玩家手牌，需要对接口返回数据做限制
             */
            DsResult handCardResult = request.execute(null, ((DsMessage)data).getData());
            System.out.println("handCardInfo: " + handCardResult);

            return handCardResult;
        });


        StateMachine<String, String, DsResult> ruleActor = getRuleActor();

        ruleActor.registerInitialState(loginState);
        ruleActor.registerState(platformState, prepareState);
        ruleActor.start();

        return this;
    }

}
