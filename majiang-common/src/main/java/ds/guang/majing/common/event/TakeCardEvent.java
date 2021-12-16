package ds.guang.majing.common.event;

import ds.guang.majing.common.event.Event;

/**
 * @author guangyong.deng
 * @date 2021-12-13 14:53
 */
public class TakeCardEvent extends Event<String> {

    private final static String id = "TakeCardEvent";

    public TakeCardEvent() {
        super(id);
    }
}
