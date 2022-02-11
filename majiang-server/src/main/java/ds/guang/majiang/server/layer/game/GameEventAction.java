package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.card.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.RoomManager;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.util.JsonUtil;
import ds.guang.majing.common.util.ResponseUtil;

import java.util.Objects;

import static ds.guang.majing.common.util.DsConstant.*;
import static java.awt.SystemColor.info;

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

        state.onEvent(EVENT_PONG_ID, data -> {
            System.out.println("对pong 事件进行处理");
            eventAction(data);
            return DsResult.ok();
        });

        state.onEvent(EVENT_IN_DIRECT_HU_ID, data -> {
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
        int eventValue = gameEvent.getEvent();
        Room room = Room.nextRound(userId, eventValue);

        // condition
        MaJiangEvent event = gameEvent.getActionEvent();

        switch (event) {
            case PONG:

                break;
            case IN_DIRECT_HU:
                // 因为有可能有多个玩家同时胡牌所以可能需要唤醒其他挂起玩家
                room.announceNext();
            default:
                break;
        }
        // 处理事件
        room.eventHandler(userId, eventValue, value);

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
                                .setEvent(
                                        new MaGameEvent()
                                                .setPlayId(userId)
                                                .setActionEvent(event)
                                                .setEventName(event.getName())
                                )
                                .setCard(card)
                    ));

            Room.write(player.id(), ResponseUtil.response(message));
        }

    }

}
