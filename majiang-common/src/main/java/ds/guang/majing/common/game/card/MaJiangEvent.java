package ds.guang.majing.common.game.card;

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

    public MaJiangEvent() {
    }

    @Override
    public void setEvent(int event) {
        this.event = event & MASK;
    }

    @Override
    public boolean isEvent() {
        return event != 0;
    }

    @Override
    public int getEvent() {
        return event;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"event\":").append(event)
                .append('}');
        return sb.toString();
    }
}
