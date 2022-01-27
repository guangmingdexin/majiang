package ds.guang.majing.client.rule.platform;

import ds.guang.majing.client.event.DsRequest;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.MaGameEvent;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.room.ClientFourRoom;
import ds.guang.majing.client.network.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.room.RoomManager;
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
            Request request = new PrepareRequest(data, GAME_URL);
            return request.execute(null);
        });

        platformState.onEvent(EVENT_MATCH_FRIEND_ID, STATE_MATCH_FRIEND_ID);

        State<String, String, DsResult> prepareState = prepareSupplier.get();

        prepareState.onEntry(data -> {

            System.out.println("进入游戏准备阶段：" );
            // 注意：这里的 data 是上一个状态的返回值，也即是玩家成功匹配后的房间信息
            // 直接进行游戏初始化操作
            // 先将 数据取出来
            GameInfoResponse response = getGameInfoResponse(data);
            Room room = response.getRoom();
            String id = response.getUserId();
            String requestNo = response.getRequestNo();

            System.out.println(".......................");
            // TODO: 后面对缓存需要更改
            // 将房间数据缓存到本地
            Cache.getInstance().setObject(preRoomInfoPrev(id), room, -1);

            DsMessage<GameInfoRequest> message = DsMessage.build(
                    EVENT_HANDCARD_ID,
                    requestNo,
                    new GameInfoRequest().setUserId(id)
            );

            // 请求手牌
            Request r = new HandCardRequest(message);
            DsResult<GameInfoResponse> rs = r.execute(null);

            // 将请求的手牌放入 room 中
            List<Integer> cards = rs.getData().getCards();
            Player p = room.findPlayerById(id);
            p.setCards(cards);

            System.out.println("room: " + room);
            System.out.println(".....................................");
            // 判断状态，是否为自己的回合
            if(room.isCurAround(id)) {
               this.stateMachine.setCurrentState(
                       STATE_TAKE_CARD_ID,
                       DsResult.data(response));
            }else {
                // 进入等待状态
                this.stateMachine.setCurrentState(
                        STATE_WAIT_ID,
                        DsResult.data(
                                new GameInfoResponse()
                                        .setUserId(id)
                        ));
                // 同时更新远程服务器状态，在这里我还需要依据当前状态，同时对服务器设置一个状态切换事件
            }
            return data;
        });

        State<String, String, DsResult> takeState = gameTakeCardStateSupplier.get();

        takeState.onEntry(data -> {

            System.out.println(".........................");
            System.out.println("进入摸牌状态");

            GameInfoResponse response = getGameInfoResponse( data);

            String userId = response.getUserId();
            String requestNo = response.getRequestNo();

            Room room = getRoomById(userId);
            Player p = room.findPlayerById(userId);

            // 进入摸牌状态
            // 触发摸牌事件 - 这是游戏初始化阶段摸牌
            DsMessage<GameInfoRequest> tm = DsMessage.build(
                    EVENT_TAKE_CARD_ID,
                    requestNo,
                    new GameInfoRequest()
                            .setUserId(userId)
            );

            Request takeCardRequest = new TakeCardRequest(tm, GAME_URL);
            // 回复消息
            DsResult<GameInfoResponse> takeCardResult = takeCardRequest.execute(null);

            System.out.println(".....................................");

            // 更新玩家手牌
            GameInfoResponse takeCardResponse = takeCardResult.getData();

            Card card = takeCardResponse.getCard();
            Integer take = (Integer) card.value();
            p.addCard(take);

            System.out.println("摸到的牌是：" + card + " id: " + userId);
            System.out.println(p);
            // 提交一个任务给 ui，并判断是否有特殊事件
            // submit(uiTask)

            GameEvent event = takeCardResponse.getEvent();

            if(event != null && !event.isEmpty()) {
                System.out.println("有事件发生！");
                // submit(uiEventTask)
            }

            // 主动切换状态 - 切换为出牌状态
            this.stateMachine.setCurrentState(STATE_TAKE_OUT_CARD_ID, takeCardResponse);

            return takeCardResult;
        });


        State<String, String, DsResult> takeOutState = gameTakeOutCardStateSupplier.get();

        takeOutState.onEntry(data -> {
           // 1.逻辑是先有
            System.out.println("...............................");
          //  System.out.println("进入出牌状态");
            return data;
        });

        takeOutState.onEvent(EVENT_TAKE_OUT_CARD_ID, STATE_WAIT_ID, data -> {

            // 1.正常摸牌之后，出牌
            // 2.其他人摸牌之后，出牌，引发玩家特殊事件（PONG），出牌
            // 3. data : 包含用户 id, 出的牌
            Request takeOutRequest = new TakeOutRequest(data, GAME_URL);
            DsResult<GameInfoResponse> takeOutResult = takeOutRequest.execute(null);
            GameInfoResponse takeOutResponse = takeOutResult.getData();

            // 更新回合
            // 获取最终位置
            String userId = takeOutResponse.getUserId();
            Room room = getRoomById(userId);

            DsMessage<GameInfoRequest> roundMessage = DsMessage.build(
                    EVENT_WAIT_ID,
                    userId,
                    new GameInfoRequest()
                            .setUserId(userId)
                            .setRequestNo(userId)


            );
            // 请求位置
            Request roundRequest = new RoundRequest(roundMessage);
            DsResult<GameInfoResponse> roundResp = roundRequest.execute(null);

            // 更新位置
            int roundIndex = roundResp.getData().getCurRoundIndex();
            room.setCurRoundIndex(roundIndex);

            return takeOutResult;

        });

        /**
         * TODO 接口的数据权限如何控制，会存在这样一种情况，该玩家利用自身 token 和 其他玩家 id
         *      获取其他 玩家手牌，需要对接口返回数据做限制
         */
        State<String, String, DsResult> waitState = gameWaitStateSupplier.get();

        waitState.onEntry(data -> {

            // 一：改变回合，设置为 -1

            // 二：等待其他玩家的游戏事件，1.其他玩家出牌， 2.其他玩家 pong/gang/hu

            // 三：请求服务器，获得最新回合，如果不是自己的回合，重新上面过程

            System.out.println("..............................");
            System.out.println("正在等待其他玩家出牌！");
            // 服务端发送的消息客户端又如何接受？
            // 只能客户端先发起请求，挂起，当服务端处理完后，再此发送
            // 请求包
            GameInfoResponse otherResponse = getGameInfoResponse(data);
            String userId = otherResponse.getUserId();


            GameInfoRequest otherRequest = new GameInfoRequest()
                    .setUserId(userId);

            DsMessage<GameInfoRequest> message = DsMessage.build(
                    EVENT_RECEIVE_OTHER_CARD_ID,
                    userId,
                    otherRequest
            );

            // 等待其他玩家出牌
            Request waitRequest = new WaitRequest(message, GAME_URL);
            DsResult<GameInfoResponse> waitResponseResult = waitRequest.execute(null);
            GameInfoResponse waitResponse = waitResponseResult.getData();

            // 提交一个 ui 任务
            String serviceName = waitResponse.getServiceName();

            // 从缓存中获取房间
            Room room = getRoomById(userId);
            Player p = room.findPlayerById(userId);

            if(serviceName.equals(EVENT_RECEIVE_OTHER_CARD_ID)) {

                // 获取其他玩家出的牌
                Card takeOut = waitResponse.getCard();
                GameEvent event = p.event(takeOut, EVENT_RECEIVE_OTHER_CARD_ID, userId);
                System.out.println("event: " + event);

                if(event != null) {

                    // 减少不必要的网络消耗
                    message.setServiceNo(EVENT_IS_GAME_EVENT_ID)
                            .setData(
                                    new GameInfoRequest()
                                        .setUserId(userId)
                            );

                    String eventStatus = otherResponse.getEventStatus();

                    while (eventStatus == null || eventStatus.equals(EVENT_STATUS_WAIT)) {

                        // 是否可以执行游戏事件
                        Request eventRequest = new EventRequest(message, GAME_URL);
                        otherResponse = getGameInfoResponse(eventRequest.execute(null));
                        eventStatus = otherResponse.getEventStatus();
                    }


                    if(eventStatus.equals(EVENT_STATUS_CANCEL)) {
                        // nothing
                    }

                    if(eventStatus.equals(EVENT_STATUS_ACTION)) {

                        // 提交一个 ui 任务
                        System.out.println();
                    }

                }
            }else if(serviceName.equals(EVENT_RECEIVE_EVENT_REPLY_ID)) {
                System.out.println("其他玩家的游戏事件");
                GameEvent event = waitResponse.getEvent();
                String otherId = waitResponse.getUserId();
                // 根据 event 提交 ui 任务
                System.out.println(otherId + " 发起了 " + event + " 任务");

            }

            // 获取最终位置
            DsMessage<GameInfoRequest> roundMessage = DsMessage.build(
                    EVENT_WAIT_ID,
                    userId,
                    new GameInfoRequest()
                            .setUserId(userId)
                            .setRequestNo(userId)


            );
            Request roundRequest = new RoundRequest(roundMessage);
            DsResult otherResult = roundRequest.execute(null);
            GameInfoResponse roundResp = getGameInfoResponse(otherResult);
            int roundIndex = roundResp.getCurRoundIndex();
            room.setCurRoundIndex(roundIndex);

            // 递归调用
            otherResponse.setRequestNo(userId);
            if(p.direction == roundIndex % room.getPlayers().length) {

                // 到了该玩家的回合
                System.out.println("到你的回合了！");
                stateMachine.setCurrentState(STATE_TAKE_CARD_ID, data);

            }else {
                System.out.println("继续等待！");
                stateMachine.setCurrentState(STATE_WAIT_ID, otherResult);
            }

            return otherResult;
        });


        StateMachine<String, String, DsResult> ruleActor = getRuleActor();

        ruleActor.registerInitialState(loginState);
        ruleActor.registerState(platformState, prepareState, takeState, takeOutState, waitState);
        ruleActor.start();

        return this;
    }

    private GameInfoResponse getGameInfoResponse(Object data) {
        DsResult<GameInfoResponse> dsResult = (DsResult<GameInfoResponse>) data;
        return dsResult.getData();
    }

    private Room getRoomById(String id) {

        return (Room) Cache.getInstance().getObject(preRoomInfoPrev(id));
    }
}
