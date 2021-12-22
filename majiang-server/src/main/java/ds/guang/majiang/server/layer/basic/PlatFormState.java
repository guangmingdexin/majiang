package ds.guang.majiang.server.layer.basic;

import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;

/**
 *
 * 登陆之后的下一个状态
 *
 * @author asus
 */
public class PlatFormState extends AbstractStateImpl<String, String, DsResult> {
    /**
     * @param id 状态ID
     */
    public PlatFormState(String id) {
        super(id);
    }

    @Override
    public State<String, String, DsResult> onEntry(Handler handler) {
        System.out.println("进入PlatForm！");
        return super.onEntry(handler);
    }
}
