package ds.guang.majing.client.rule.platform;

import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.room.ClientFourRoom;
import ds.guang.majing.client.network.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.util.DsConstant;
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


    /**
     * 游戏状态 - 摸牌， 出牌, 等待
     */
    private Supplier<State<String, String, DsResult>> gameTakeCardStateSupplier = () -> stateStrategy.newState(() -> STATE_TAKE_CARD_ID);

    private Supplier<State<String, String, DsResult>> gameTakeOutCardStateSupplier = () -> stateStrategy.newState(() -> STATE_TAKE_OUT_CARD_ID);

    private Supplier<State<String, String, DsResult>> gameWaitStateSupplier = () -> stateStrategy.newState(() -> STATE_WAIT_ID);

    @Override
    public Rule<String, StateMachine<String, String, DsResult>> create(String s) {

        State<String, String, DsResult> loginState = loginStateSupplier.get();
        loginState.onEvent(EVENT_LOGIN_ID, STATE_PLATFORM_ID, data -> {
            // 我应该把这里作为一个异步任务
            Request request = new LoginRequest(data);
            return request.execute(null);
        });

        loginState.onExit(data -> {
            System.out.println("登录成功！");
            return data;
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
            String id = attr.get("requestNo").toString();
            System.out.println(".......................");
            // TODO: 后面对缓存需要更改
            // 将房间数据缓存到本地
            Cache.getInstance().setObject(preRoomInfoPrev(id), room, -1);

            DsMessage<String> message = DsMessage.build(EVENT_HANDCARD_ID, id, id);

            // 请求手牌
            Request r = new HandCardRequest(message);
            DsResult<List<Integer>> rs = r.execute(null);

            // 将请求的手牌放入 room 中
            List<Integer> cards = rs.getData();
            Player p = room.findPlayerById(id);
            p.setCards(cards);
            System.out.println("room: " + room);

            System.out.println(".....................................");
            // 判断状态，是否为自己的回合
            if(room.isCurAround(id)) {
                // 进入摸牌状态
                // 触发摸牌事件 - 这是游戏初始化阶段摸牌
                DsMessage<String> tm = DsMessage.build(EVENT_TAKE_CARD_ID, id, id);
                Request takeCardRequest = new TakeCardRequest(tm);
                DsResult<GameInfoResponse> tRs = takeCardRequest.execute(null);

                System.out.println(".....................................");

                // 更新玩家手牌
                GameInfoResponse response = tRs.getData();
                Card card = response.getCard();
                Integer take = (Integer) card.value();
                p.addCard(take);
                System.out.println("摸到的牌是：" + card + " id: " + id);
                System.out.println(p);
                // 提交一个任务给 ui，并判断是否有特殊事件
                // submit(uiTask)

                Map<MaJiangEvent, Integer> event = response.getEvent();

                if(!event.isEmpty()) {
                    System.out.println("有事件发生！");
                    // submit(uiEventTask)
                }

                // 主动切换状态 - 切换为出牌状态
                this.stateMachine.setCurrentState(STATE_TAKE_OUT_CARD_ID, tRs);
            }else {
                // 进入等待状态
                this.stateMachine.setCurrentState(STATE_WAIT_ID, DsResult.data(new GameInfoResponse(id, null, null)));
                // 同时更新远程服务器状态，在这里我还需要依据当前状态，同时对服务器设置一个状态切换事件
                // 不如直接

            }
            return data;
        });

        State<String, String, DsResult> takeState = gameTakeCardStateSupplier.get();

        takeState.onEntry(data -> {

            System.out.println(".........................");
          //  System.out.println("进入摸牌状态");

            return data;
        });


        State<String, String, DsResult> takeOutState = gameTakeOutCardStateSupplier.get();

        takeOutState.onEntry(data -> {
           // 1.逻辑是先有
            System.out.println("...............................");
          //  System.out.println("进入出牌状态");
            return this;
        });

        takeOutState.onEvent(EVENT_TAKE_OUT_CARD_ID, STATE_WAIT_ID, data -> {

            // 1.正常摸牌之后，出牌
            // 2.其他人摸牌之后，出牌，引发玩家特殊事件（PONG），出牌
            // 3. data : 包含用户 id, 出的牌
            Request request = new TakeOutRequest(data);
            return (DsResult<GameInfoResponse>) request.execute(null);

        });

        /**
         * TODO 接口的数据权限如何控制，会存在这样一种情况，该玩家利用自身 token 和 其他玩家 id
         *      获取其他 玩家手牌，需要对接口返回数据做限制
         */
        State<String, String, DsResult> waitState = gameWaitStateSupplier.get();
        waitState.onEntry(data -> {

            System.out.println("..............................");
            System.out.println("正在等待其他玩家出牌！");
            // 服务端发送的消息客户端又如何接受？
            // 只能客户端先发起请求，挂起，当服务端处理完后，再此发送
            DsResult<GameInfoResponse> r = (DsResult<GameInfoResponse>) data;
            System.out.println("data: " + data);
            // 请求包
            GameInfoRequest rt = new GameInfoRequest(r.getData().getUserId(), MaJiangEvent.NOTHING);
            DsMessage<GameInfoRequest> message = DsMessage.build(EVENT_WAIT_ID, rt.getUserId(), rt);
            Request request = new WaitRequest(message);
            DsResult<GameInfoResponse> execute = request.execute(null);
            System.out.println("execute: " + execute);
            return execute;
        });


        StateMachine<String, String, DsResult> ruleActor = getRuleActor();

        ruleActor.registerInitialState(loginState);
        ruleActor.registerState(platformState, prepareState, takeState, takeOutState, waitState);
        ruleActor.start();

        return this;
    }

}
