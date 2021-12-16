package ds.guang.majing.common.state;

/**
 * @author guangyong.deng
 * @date 2021-12-13 11:32
 */
public class HandOutCardState extends State{

    private final static String id = "HandOutCardState";
    /**
     *
     */
    public HandOutCardState() {
        super(id);
    }

    @Override
    public void action(Context context) {
        System.out.println("player hand out one card!");
        context.setState(this);
    }

    @Override
    public State next() {
        return null;
    }
}
