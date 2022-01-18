package ds.guang.majing.client.rule.platform;

import ds.guang.majing.client.entity.ClientFourRoom;
import ds.guang.majing.client.network.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.util.JsonUtil;
import ds.guang.majing.common.factory.DefaultStateStrategyFactory;
import ds.guang.majing.common.factory.StateStrategy;
import ds.guang.majing.common.factory.StateStrategyFactory;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.rule.AbstractRule;
import ds.guang.majing.common.game.rule.Rule;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static ds.guang.majing.common.util.DsConstant.*;

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

    private Supplier<State<String, String, DsResult>> initSupplier = () -> stateStrategy.newState(() -> STATE_PREPARE_ID);

    @Override
    public Rule<String, StateMachine<String, String, DsResult>> create(String s) {

        State<String, String, DsResult> loginState = loginStateSupplier.get();
        loginState.onEvent(EVENT_LOGIN_ID, STATE_PLATFORM_ID, data -> {
            // 我应该把这里作为一个异步任务
            Request request = new LoginRequest(data);
            return request.execute(null);
        });

        State<String, String, DsResult> platformState = platformSupplier.get();
        platformState.onEntry(data -> {
            // 1.获取客户端资源
            return null;
        });
        platformState.onEvent(EVENT_PREPARE_ID, STATE_PREPARE_ID, data -> {
            // 构造一个 LoginRequest 对象
            Request request = new PrepareRequest(data);
            return request.execute(null);
        });
        platformState.onEvent(EVENT_MATCH_FRIEND_ID, STATE_MATCH_FRIEND_ID);


        State<String, String, DsResult> prepareState = prepareSupplier.get();
        prepareState.onEntry(data -> {
            System.out.println("进入游戏准备阶段：" );
            // 注意：这里的 data 是上一个状态的返回值，也即是玩家成功匹配后的房间信息
            // 直接进行游戏初始化操作
            // 先将 数据取出来
            DsResult dsResult = (DsResult) data;
            Room room = (Room) JsonUtil.mapToObj(dsResult.getData(), ClientFourRoom.class);
            Map<String, Object> attr = dsResult.getAttrMap();
            String requestNo = attr.get("requestNo").toString();

            Request request = new InitRequest(DsMessage.build("-1", "-1", null));
            request.execute(() -> {

                DsMessage<String> message = DsMessage.build(EVENT_GET_HANDCARD_ID, requestNo, requestNo);

                // 请求手牌
                Request r = new GetHandCardRequest(message);
                DsResult<List<Integer>> rs = r.execute(null);
                System.out.println("rs: " + rs);
                List<Integer> cards = rs.getData();
                Player p = room.findPlayerById(requestNo);
                p.setCards(cards);
                System.out.println("room: " + room);
            });

            // 判断状态，是否为自己的回合
            if(room.isCurAround(requestNo)) {
                // 触发摸牌事件
                DsMessage<String> message = DsMessage.build(EVENT_POST_TAKE_CARD_ID, requestNo, requestNo);
                Request takeCardRequest = new PostTakeCardRequest(message);
                takeCardRequest.execute(null);

            }else {
                throw new IllegalArgumentException("非本回合不能摸牌！");
            }

            return data;
        });

        State<String, String, DsResult> initState = initSupplier.get();


//
//            /**
//             * TODO 接口的数据权限如何控制，会存在这样一种情况，该玩家利用自身 token 和 其他玩家 id
//             *      获取其他 玩家手牌，需要对接口返回数据做限制
//             */
//            DsResult handCardResult = request.execute(null);
//            System.out.println("handCardInfo: " + handCardResult);
//
//            return handCardResult;
//        });

        StateMachine<String, String, DsResult> ruleActor = getRuleActor();

        ruleActor.registerInitialState(loginState);
        ruleActor.registerState(platformState, prepareState);
        ruleActor.start();

        return this;
    }

}
