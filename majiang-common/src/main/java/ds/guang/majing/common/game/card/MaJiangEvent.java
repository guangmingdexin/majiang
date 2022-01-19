package ds.guang.majing.common.game.card;

/**
 * @author guangyong.deng
 * @date 2022-01-19 10:20
 */
public enum MaJiangEvent {

    PONG(1, "碰"),
    SELF_GANG(2, "暗杠"),
    DIRECT_GANG(3, "直杠"),
    IN_DIRECT_GANG(4, "巴杠"),
    SELF_HU(5, "自摸"),
    IN_DIRECT_HU(6, "点炮"),

    NOTHING(-1, "忽略")
    ;


    /**
     * 特殊事件值
     */
    private int value;


    /**
     * 描述
     */
    private String desc;


    MaJiangEvent(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
