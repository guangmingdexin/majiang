package ds.guang.majing.common.card;

/**
 *
 * 麻将信息
 * @author asus
 */
public class MaJiangEvent implements GameEvent {


    private int event;


    public static final int PONG_EVENT = 1;


    public static final int GANG_EVENT = 1 << 1;


    public static final int HU_EVENT = 1 << 2;


    private static final int MASK = 111;

    @Override
    public void setEvent(int event) {
        this.event = event & MASK;
    }
}
