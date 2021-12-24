package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;

import static ds.guang.majing.common.DsConstant.*;

/**
 *
 * 登陆之后的下一个状态
 *
 * @author asus
 */
@StateMatchAction(value = STATE_PLATFORM_ID)
public class PlatFormAction implements Action {

    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            System.out.println("进入平台！");
            return null;
        });

        state.onEvent(EVENT_PLATFORM_ID, STATE_PREPARE_ID);
        state.onEvent(EVENT_MATCH_FRIEND_ID, STATE_MATCH_FRIEND_ID);
    }


//    @Override
//    public State<String, String, DsResult> onEntry(Handler handler) {
//        System.out.println("进入PlatForm！");
//        return super.onEntry(handler);
//    }
}
