package ds.guang.majing.common.game.card;

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

   public final static CardType generate(int num) {

        if(num >= 11 && num <= 19) {
            return CardType.CHARACTER;
        }

        if(num >= 111 && num <= 119) {
            return CardType.BAMBOO;
        }

        if(num >= 1111 && num <= 1119) {
            return CardType.DOT;
        }

        throw new IllegalArgumentException("参数错误");
    }
}
