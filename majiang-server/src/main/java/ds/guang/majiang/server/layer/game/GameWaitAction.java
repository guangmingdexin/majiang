package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.state.State;

import static ds.guang.majing.common.util.DsConstant.EVENT_RECEIVE_OTHER_CARD_ID;
import static ds.guang.majing.common.util.DsConstant.STATE_WAIT_ID;

/**
 * @author guangyong.deng
 * @date 2022-01-20 17:31
 */
@StateMatchAction(STATE_WAIT_ID)
public class GameWaitAction implements Action {

    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            System.out.println("进入等待状态！");
            return data;
        });
    }
}
