package ds.guang.majing.common.state;

/**
 * @author guangyong.deng
 * @date 2021-12-13 11:46
 */
public class ThinkState extends  State {

    private final static String id = "ThinkState";
    /**
     *
     */
    public ThinkState() {
        super(id);
    }


    @Override
    public void action(Context context) {
        System.out.println("player enter thinking time!");
    }

    @Override
    public State next() {
        return null;
    }
}
