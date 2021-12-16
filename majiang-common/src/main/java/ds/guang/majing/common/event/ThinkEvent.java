package ds.guang.majing.common.event;

/**
 * @author guangyong.deng
 * @date 2021-12-13 14:52
 */
public class ThinkEvent extends Event<String> {

    private final static String id = "ThinkEvent";

    public ThinkEvent() {
        super(id);
    }


}
