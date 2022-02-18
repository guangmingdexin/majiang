package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.room.ServerFourRoom;
import ds.guang.majing.common.state.State;
import ds.guang.majiang.server.game.ResponseUtil;

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
            System.out.println("判断事件能不能执行！");
            GameInfoRequest request = ResponseUtil.getGameInfoRequest(data);
            // 通知客户端，并确定是否能够执行游戏事件
            String userId = request.getUserId();
            ServerFourRoom room = ServerFourRoom.getRoomById(userId);
            room.announce(userId);
            return DsResult.data(data);
        });

    }

}
