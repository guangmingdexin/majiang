package ds.guang.majing.common.card;

/**
 * @author guangmingdexin
 */

public enum CardType {

    /**
     *  条子
     */
    BAMBOO("bamboo", 0),

    /**
     * 万子
     */
    CHARACTER("character", 1),

    /**
     * 筒子
     */
    DOT("dot", 2);

    private int value;

    private String patten;

    CardType(String patten, int value) {
        this.patten = patten;
        this.value = value;
    }
}
