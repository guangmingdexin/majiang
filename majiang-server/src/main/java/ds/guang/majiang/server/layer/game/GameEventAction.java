package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.RoomManager;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.util.JsonUtil;

import java.util.Objects;

import static ds.guang.majing.common.util.DsConstant.*;
import static java.awt.SystemColor.info;

/**
 * @author guangyong.deng
 * @date 2022-01-19 9:27
 */
@StateMatchAction(STATE_EVENT_ID)
public class GameEventAction implements Action {


    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {

        state.onEntry(data -> {

            System.out.println("有事件发生！");
            return data;
        });

        // 触发 PONG 事件的前一个状态 一定只有一种情况
        // 1.其他玩家出牌，当前玩家状态由 wait -> event 传入的数据一定包括 接受到的其他玩家的牌
        state.onEvent(EVENT_PONG_ID, data -> {

            DsResult<GameInfoResponse> rs = (DsResult<GameInfoResponse>) data;
            // 1.检查服务端手牌是否符合要求


            return data;
        });


        state.onEvent(EVENT_SELF_GANG_ID, data -> {
            // 1.获取房间号，获取玩家手牌
           // System.out.println("-------------" + id + " 杠牌：" + request + "-----------------");

            return this;
        });


        state.onEvent(EVENT_SELF_HU_ID, data -> {

            return this;
        });
    }
}
