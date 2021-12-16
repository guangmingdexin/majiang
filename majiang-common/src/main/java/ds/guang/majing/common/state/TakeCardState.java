package ds.guang.majing.common.state;

/**
 * @author guangyong.deng
 * @date 2021-12-13 11:30
 */
public class TakeCardState extends  State {

    private final static String id = "TakeCardState";
    /**
     *
     */
    public TakeCardState() {
        super(id);
    }

    @Override
    public void action(Context context) {
        // 调用远程服务器接口，摸一张牌
        System.out.println("player take one card!");
        context.setState(this);
    }

    @Override
    public State next() {
        return null;
    }
}
