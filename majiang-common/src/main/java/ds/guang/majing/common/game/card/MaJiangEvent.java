package ds.guang.majing.common.game.card;

/**
 * @author guangyong.deng
 * @date 2022-01-19 10:20
 */
public enum MaJiangEvent {
    /**
     * 特殊事件
     */
    PONG(1, "碰", -1),
    SELF_GANG(2, "暗杠", 0),
    DIRECT_GANG(3, "直杠", 0),
    IN_DIRECT_GANG(4, "巴杠", 0),
    SELF_HU(5, "自摸", 1),
    IN_DIRECT_HU(6, "点炮", 1),
    NOTHING(-1, "忽略", -99)
    ;


    /**
     * 特殊事件值
     */
    private int value;


    /**
     * 描述
     */
    private String desc;

    /**
     * 事件的优先级
     */
    private int priority;


    MaJiangEvent(int value, String desc, int priority) {
        this.value = value;
        this.desc = desc;
        this.priority = priority;
    }


    public int getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() { return desc;}


    public static MaJiangEvent generate(int eventValue) {

        for (MaJiangEvent event : values()) {
            if(event.getValue() == eventValue) {
                return event;
            }
        }

        throw new IllegalArgumentException("错误的事件");
    }
}
