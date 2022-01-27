package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEventHandler;
import ds.guang.majing.common.game.card.MaJiangEvent;
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
        int value = (int) request.getCard().value();

        int eventValue = request.getEvent().getEvent();
        Room room = Room.nextRound(userId, eventValue);

        // condition
        MaJiangEvent event = MaJiangEvent.generate(eventValue);

        switch (event) {
            case PONG:
                room.remove(userId, value);
                break;
            case IN_DIRECT_HU:
                room.announceNext();
            default:
                break;
        }

        room.addEventCard(userId, value, eventValue);

        for (Player player : room.getPlayers()) {

            DsMessage<DsResult<GameInfoResponse>> message = DsMessage.build(
                    EVENT_RECEIVE_EVENT_REPLY_ID,
                    userId,
                    new GameInfoResponse()
                            .setUserId(userId)
                            .setEventStatus("COMPLETE")
                            .setCurRoundIndex(room.getCurRoundIndex())
            );

            Room.write(player.id(), ResponseUtil.response(message));
        }

    }

}
