package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.machines.StateMachines;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.RoomManager;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;

import java.util.Objects;
import java.util.concurrent.SynchronousQueue;

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


        state.onEvent(EVENT_RECEIVE_OTHER_CARD_ID, data -> {

            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;
            GameInfoRequest request = message.getData();
            String id = request.getUserId();

            Player p = RoomManager.findRoomById(id).findPlayerById(id);

            return DsResult.ok("其他玩家已经出牌！");
        });

        state.onEvent(EVENT_RECEIVE_EVENT_REPLY_ID, data -> {

            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;
            GameInfoRequest request = message.getData();
            String id = request.getUserId();


            // 有如下几种情况
            // 玩家 A 出一张牌：
            // 一：玩家 B 可以 PONG， 玩家 C、D 无动作，此时回合由 A -> (B) -> C
            // 二：玩家 B 可以 GANG, 玩家 C、D 无动作，此时回合由 A -> B
            // 三：玩家 B 可以 hu，玩家 C、D 无动作，此时回合由 A -> (B) -> C
            // 四：玩家 B 可以 PONG 或者 hu，此时 玩家 C 或者 D 可以 hu A -> C/D

            return DsResult.ok("点击成功");
        });


        state.onEvent(EVENT_IS_GAME_EVENT_ID, data -> {

            // 1.获取房间
            // 2.获取事件处理器


            return data;
        });
    }
}
