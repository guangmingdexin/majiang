package ds.guang.majing.client.rule;

import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.javafx.task.OperationTask;
import ds.guang.majing.client.javafx.ui.action.OperationAction;
import ds.guang.majing.client.cache.Cache;
import ds.guang.majing.client.remote.dto.ao.UserQueryAo;
import ds.guang.majing.client.remote.service.IUserService;
import ds.guang.majing.client.remote.service.UserService;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.client.game.ClientFourRoom;
import ds.guang.majing.client.network.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.util.ClassUtil;
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
import javafx.application.Platform;

import java.util.List;
import java.util.function.Supplier;

import static ds.guang.majing.client.cache.CacheUtil.getRoomById;
import static ds.guang.majing.common.game.card.MaJiangEvent.OVER;
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


    private Supplier<State<String, String, DsResult>> startSupplier = () -> stateStrategy.newState(() -> STATE_START_ID);

    private Supplier<State<String, String, DsResult>> prepareSupplier = () -> stateStrategy.newState(() -> STATE_PREPARE_ID);


    /**
     * 游戏状态 - 摸牌， 出牌, 等待
     */
    private Supplier<State<String, String, DsResult>> gameTakeCardStateSupplier = () -> stateStrategy.newState(() -> STATE_TAKE_CARD_ID);

    private Supplier<State<String, String, DsResult>> gameTakeOutCardStateSupplier = () -> stateStrategy.newState(() -> STATE_TAKE_OUT_CARD_ID);

    private Supplier<State<String, String, DsResult>> gameWaitStateSupplier = () -> stateStrategy.newState(() -> STATE_WAIT_ID);

    @Override
    public Rule<String, StateMachine<String, String, DsResult>> create(String s) {


        State<String, String, DsResult> prepareState = prepareSupplier.get();


        prepareState.onEvent(EVENT_RANDOM_MATCH_ID, data -> {

            // 开始进行游戏的匹配
            // 1.发送匹配消息给服务端
            System.out.println("开始游戏匹配");
            String userId = CacheUtil.getUserId();

            IUserService userService = new UserService();

            GameUser gameUser = userService.getOne(new UserQueryAo(userId));

            if(gameUser == null) {
                throw new IllegalArgumentException("用户服务调取失败");
            }

            System.out.println("gameUser: " + gameUser);

            GameInfoRequest randomMatchReq = new GameInfoRequest()
                    .setUserId(userId)
                    .setGameUser(gameUser);

            DsMessage<GameInfoRequest> randomMatchMsg = DsMessage.build(EVENT_RANDOM_MATCH_ID, userId, randomMatchReq);

            Request request = new PrepareRequest(randomMatchMsg);

            DsResult<GameInfoResponse> matchResp = request.execute(null);

            System.out.println("进入游戏准备阶段：" );
            // 注意：这里的 data 是上一个状态的返回值，也即是玩家成功匹配后的房间信息
            // 直接进行游戏初始化操作
            // 先将 数据取出来
            GameInfoResponse response = getGameInfoResponse(matchResp);

            // 类型转换
            Room target = response.getRoom();
            Room room = new ClientFourRoom();
            ClassUtil.convert(target, room);

            String id = response.getUserId();

            System.out.println(".......................");
            // TODO: 后面对缓存需要更改
            // 将房间数据缓存到本地
            Cache.getInstance().setObject(preRoomInfoPrev(id), room, -1);

            DsMessage<GameInfoRequest> message = DsMessage.build(
                    EVENT_HANDCARD_ID,
                    id,
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
            return rs;
        });

        State<String, String, DsResult> takeState = gameTakeCardStateSupplier.get();

        takeState.onEntry(data -> {

            System.out.println(".........................");
            System.out.println("进入摸牌状态");

            GameInfoResponse response = getGameInfoResponse(data);
            String userId = response.getUserId();

            Room room = getRoomById(userId);
            Player p = room.findPlayerById(userId);

            // 进入摸牌状态
            // 触发摸牌事件 - 这是游戏初始化阶段摸牌
            DsMessage<GameInfoRequest> tm = DsMessage.build(
                    EVENT_TAKE_CARD_ID,
                    userId,
                    new GameInfoRequest()
                            .setUserId(userId)
            );

            Request takeCardRequest = new TakeCardRequest(tm);
            // 回复消息
            DsResult<GameInfoResponse> takeCardResult = takeCardRequest.execute(null);

            System.out.println(".....................................");

            // 更新玩家手牌
            GameInfoResponse takeCardResponse = takeCardResult.getData();

            GameEvent event = takeCardResponse.getEvent();
            
            if(event != null && event.getEvent() == OVER.getValue()) {
                // 进入 over 状态
                this.stateMachine.setCurrentState(STATE_GAME_OVER_ID, room);
            }

            Card card = takeCardResponse.getCard();
            int take = card.value();
            p.addCard(take);

            System.out.println("摸到的牌是：" + card + " id: " + userId);
            System.out.println(p);
            // 提交一个任务给 ui，并判断是否有特殊事件
            // submit(uiTask)
            

            if(event != null && !event.isEmpty()) {
                System.out.println("有事件发生！");

                Platform.runLater(OperationTask.getInstance()
                        .setCard(card)
                        .setGameEvent(event)
                        .setMessage(
                                DsMessage.build(null, userId, null)
                        )
                );
            }else {
                // 主动切换状态 - 切换为出牌状态
                this.stateMachine.setCurrentState(STATE_TAKE_OUT_CARD_ID, takeCardResponse);
            }

            return takeCardResult;
        });

        State<String, String, DsResult> takeOutState = gameTakeOutCardStateSupplier.get();

        takeOutState.onEntry(data -> {
           // 1.逻辑是先有
            System.out.println("...............................");
            System.out.println("进入出牌状态");
            // 2.先将自身状态设置为不能再次出牌，防止多次出牌
            return data;
        });

        takeOutState.onEvent(EVENT_TAKE_OUT_CARD_ID, STATE_WAIT_ID, data -> {
            // 1.正常摸牌之后，出牌
            // 2.其他人摸牌之后，出牌，引发玩家特殊事件（PONG），出牌
            // 3. data : 包含用户 id, 出的牌

            // 更新回合
            // 获取最终位置
            GameInfoRequest takeOutInfo = ((DsMessage<GameInfoRequest>) data).getData();
            String userId = takeOutInfo.getUserId();
            Room room = getRoomById(userId);
            room.setCurRoundIndex(-1);

            Request takeOutRequest = new TakeOutRequest(data);
            DsResult<GameInfoResponse> takeOutResult = takeOutRequest.execute(null);
            GameInfoResponse takeOutResponse = takeOutResult.getData();

            room.remove(userId, takeOutInfo.getCard().value());

            // 更新位置
            int roundIndex = takeOutResponse.getCurRoundIndex();
            // RoundIndex 应该是线程安全的
            room.setCurRoundIndex(roundIndex);
            room.setPrevRoundIndex(takeOutResponse.getPrevRoundIndex());

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
            Request waitRequest = new WaitRequest(message);
            DsResult<GameInfoResponse> waitResponseResult = waitRequest.execute(null);
            otherResponse = waitResponseResult.getData();

            // 提交一个 ui 任务
            String serviceName = otherResponse.getServiceName();

            // 从缓存中获取房间
            Room room = getRoomById(userId);
            Player p = room.findPlayerById(userId);

            System.out.println("service-name...." + serviceName);

            if(serviceName.equals(EVENT_RECEIVE_OTHER_CARD_ID)) {

                // 获取其他玩家出的牌
                Card takeOut = otherResponse.getCard();
                GameEvent event = p.event(takeOut, EVENT_RECEIVE_OTHER_CARD_ID, userId);
                System.out.println(" 其他玩家出的牌是： " + takeOut + " event: " + event);

                if(event != null) {

                    // 减少不必要的网络消耗
                    message.setServiceNo(EVENT_IS_GAME_EVENT_ID)
                            .setRequestNo(userId)
                            .setData(
                                    new GameInfoRequest()
                                        .setUserId(userId)
                            );

                    String eventStatus = otherResponse.getEventStatus();

                    while (eventStatus == null || eventStatus.equals(EVENT_STATUS_WAIT)) {

                        // 是否可以执行游戏事件
                        Request eventRequest = new EventRequest(message);
                        waitResponseResult = eventRequest.execute(null);
                        otherResponse = getGameInfoResponse(waitResponseResult);
                        eventStatus = otherResponse.getEventStatus();
                    }

                    if(eventStatus.equals(EVENT_STATUS_CANCEL)) {
                        // nothing
                        System.out.println("取消任务");
                        // Platform.runLater(OperationTask.getInstance());
                    }

                    if(eventStatus.equals(EVENT_STATUS_ACTION)) {
                        // 提交一个 ui 任务

                        Platform.runLater(OperationTask.getInstance()
                                .setGameEvent(otherResponse.getEvent())
                                .setMessage(
                                        DsMessage.build(null, userId, null)
                                )
                                .setCard(takeOut)
                        );
                    }
                }else {
                    next(otherResponse, waitResponseResult, room, p);
                }
            }else if(serviceName.equals(EVENT_RECEIVE_EVENT_REPLY_ID)) {
                System.out.println("其他玩家的游戏事件");
                GameEvent event = otherResponse.getEvent();
                String otherId = event.getPlayId();
                // 根据 event 提交 ui 任务
                System.out.println(otherId + " 发起了 " + event + " 任务");

                // 渲染界面，同时更新回合
                Platform.runLater(new OperationAction());

                next(otherResponse, waitResponseResult, room, p);
            }else if(serviceName.equals(EVENT_OVER_ID)) {

                // 1.摸牌的时候被其他玩家通知游戏结束
                // 2.其他玩家胡牌之后，如果是当前玩家的回合，会发起摸牌请求，此时无论服务端是否已经发送结束消息，最后都可以正常执行

                System.out.println("游戏结束了");

                stateMachine.setCurrentState(STATE_GAME_OVER_ID, null);
            }

            return waitResponseResult;
        });

        waitState.onExit(data -> {
            System.out.println("wait 状态结束了！");
            return data;
        });


        StateMachine<String, String, DsResult> ruleActor = getRuleActor();

        // 缓存
        Cache.getInstance().setObject(preUserMachinekey("machine-1"), ruleActor, -1);

        State<String, String, DsResult> startState = startSupplier.get();

        ruleActor.registerInitialState(startState);
        ruleActor.registerState(prepareState, takeState, takeOutState, waitState);
        ruleActor.start();

        return this;
    }

    private void next(GameInfoResponse otherResponse,  DsResult<GameInfoResponse> waitResponseResult, Room room, Player p) {
        System.out.println("otherResponse: " + otherResponse);
        int roundIndex = otherResponse.getCurRoundIndex();
        room.setCurRoundIndex(roundIndex);
        System.out.println("after roundIndex: " + room.getCurRoundIndex());
        // 递归调用
        if(p.direction == roundIndex % room.getPlayers().length) {

            // 到了该玩家的回合
            System.out.println("到你的回合了！");
            stateMachine.setCurrentState(STATE_TAKE_CARD_ID, waitResponseResult);

        }else {
            System.out.println("继续等待！");
            // 似乎有内存泄漏的风险，每次进入下一回合，又会重新进入 entry，
            // 直到下次进入自身回合才能释放
            stateMachine.setCurrentState(STATE_WAIT_ID, waitResponseResult);
        }
    }

    private GameInfoResponse getGameInfoResponse(Object data) {
        DsResult<GameInfoResponse> dsResult = (DsResult<GameInfoResponse>) data;
        return dsResult.getData();
    }
}
