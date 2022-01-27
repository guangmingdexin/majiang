package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.machines.StateMachines;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.RoomManager;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;
import ds.guang.majing.common.util.ResponseUtil;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2022-01-20 17:31
 */
@SuppressWarnings("unchecked")
@StateMatchAction(STATE_WAIT_ID)
public class GameWaitAction implements Action {

    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            System.out.println("进入等待状态！");
            return data;
        });


        state.onEvent(EVENT_IS_GAME_EVENT_ID, STATE_EVENT_ID, data -> {

            GameInfoRequest request = ResponseUtil.getGameInfoRequest(data);
            // 通知客户端，并确定是否能够执行游戏事件
            Room.announce(request.getUserId());

            return DsResult.data(data);
        });


        state.onEvent(EVENT_WAIT_ID, data -> {

            System.out.println("返回最新的位置： ");

            GameInfoRequest request = ResponseUtil.getGameInfoRequest(data);

            String userId = request.getUserId();
            Room room = Room.getRoomById(userId);

            DsMessage<DsResult<GameInfoResponse>> message = DsMessage.build(
                    EVENT_RECEIVE_EVENT_REPLY_ID,
                    userId,
                    DsResult.data(new GameInfoResponse()
                            .setUserId(userId)
                            .setCurRoundIndex(room.getCurRoundIndex())
                    ));

            Room.write(userId, ResponseUtil.response(message));


            if(room.isCurAround(userId)) {

                StateMachine<String, String, DsResult> stateMachine = StateMachines.get(preUserMachinekey(userId));
                stateMachine.setCurrentState(STATE_TAKE_CARD_ID, data);
            }

            return DsResult.ok();
        });

    }

}
