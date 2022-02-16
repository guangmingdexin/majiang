package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.state.State;

import static ds.guang.majing.common.util.DsConstant.STATE_GAME_OVER_ID;

/**
 * @author guangyong.deng
 * @date 2022-02-14 13:57
 */
@StateMatchAction(value = STATE_GAME_OVER_ID)
public class GameOverAction implements Action {

    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            // 统计相关信息，设置玩家状态
            // data: id
            System.out.println("该玩家游戏结束");
            return data;
        });
    }




}
