package ds.guang.majing.common.state;

import ds.guang.majing.common.DsResult;

/**
 * @author guangyong.deng
 * @date 2021-12-13 11:35
 */
public class PongState extends  State<String, String, DsResult> {

    private final static String id = "PongState";

    /**
     *
     */
    public PongState() {
        super(id);
    }

    @Override
    public void action(Context context) {
        System.out.println("player pong one card!");
        context.setState(this);
        // 如何进入下一个 state
    }

    @Override
    public State next() {
        return null;
    }
}
