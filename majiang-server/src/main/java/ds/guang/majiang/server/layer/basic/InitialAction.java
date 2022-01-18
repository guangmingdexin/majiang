package ds.guang.majiang.server.layer.basic;


import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.state.State;

import static ds.guang.majing.common.util.DsConstant.STATE_INITIAL_ID;

/**
 * @author guangmingdexin
 */
@StateMatchAction(value = STATE_INITIAL_ID)
public class InitialAction implements Action {


    @Override
    public void handler(State state) {

    }
}
