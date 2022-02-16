package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.MaGameEvent;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.machines.StateMachines;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.ServerFourRoom;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;
import ds.guang.majing.common.util.ResponseUtil;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2022-01-19 9:27
 */
@StateMatchAction(value = STATE_EVENT_ID)
public class GameEventAction implements Action {


    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            System.out.println("有事件发生！");
            return data;
        });

        state.onEvent(EVENT_PONG_ID, STATE_TAKE_OUT_CARD_ID, data -> {
            System.out.println("对pong 事件进行处理");
            eventAction(data);
            return DsResult.ok();
        });

        state.onEvent(EVENT_GANG_ID, STATE_TAKE_CARD_ID, data -> {
            System.out.println("杠处理");
            eventAction(data);
            return DsResult.ok();
        });

        state.onEvent(EVENT_HU_ID, data -> {
            eventAction(data);
            return DsResult.ok();
        });


        state.onEvent(EVENT_IGNORE_ID, data -> {
            // 1.第一种情况，下一个回合是自己，则进入摸牌状态
            // 2.第二种情况，下一个回合不是自己，则进入等待状态
            eventAction(data);
            return DsResult.ok();
        });

    }

    @SuppressWarnings("unchecked")
    private void eventAction(Object data) {

        GameInfoRequest request = ResponseUtil.getGameInfoRequest(data);

        String userId = request.getUserId();
        Card card = request.getCard();
        int value =  card.value();

        MaGameEvent gameEvent = (MaGameEvent)request.getEvent();

        ServerFourRoom room = ServerFourRoom.getRoomById(userId);
        // 处理事件（包括两部分-回合切换，事件处理）
        room.eventHandler(gameEvent, value);

        // 状态切换或者其他处理
        MaJiangEvent event = gameEvent.getActionEvent();

        if (event == MaJiangEvent.IN_DIRECT_HU) {
            // 因为有可能有多个玩家同时胡牌所以可能需要唤醒其他挂起玩家
            room.announceNext();
        }

        for (Player player : room.getPlayers()) {

            DsMessage<DsResult<GameInfoResponse>> message = DsMessage.build(
                    EVENT_RECEIVE_EVENT_REPLY_ID,
                    player.id(),
                    DsResult.data(
                            new GameInfoResponse()
                                .setServiceName(EVENT_RECEIVE_EVENT_REPLY_ID)
                                .setUserId(player.id())
                                .setEventStatus("COMPLETE")
                                    // 判断是否还有其他事件需要执行，如果没有则可以进入下一个回合
                                .setCurRoundIndex(room.getEventHandler().isEmpty() ? room.getCurRoundIndex() : -1)
                                .setPrevRoundIndex(room.getPrevRoundIndex())
                                .setEvent(
                                        new MaGameEvent()
                                                .setPlayId(userId)
                                                .setActionEvent(event)
                                                .setEventName(event.getName())
                                )
                                .setCard(card)
                    ));

            ServerFourRoom.write(player.id(), ResponseUtil.response(message));
        }

    }

}
