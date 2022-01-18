package ds.guang.majing.common.game.card;

/**
 * @author guangmingdexin
 */
public class MaJiang implements Card {

    /**
     * 11-19 --- 代表 一万到九万
     * 101 - 109 --- 代表 一条到九条
     * 1001 - 1009 --- 代表 一筒到九筒
     */
    private int value;

    private CardType cardType;

    public MaJiang() {
    }

    public MaJiang(int value, CardType cardType) {
        this.value = value;
        this.cardType = cardType;
    }

    public int getValue() {
        return value;
    }

    public MaJiang setValue(int value) {
        this.value = value;
        return this;
    }


    public CardType getCardType() {
        return cardType;
    }

    public MaJiang setCardType(CardType cardType) {
        this.cardType = cardType;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"value\":").append(value)
                .append(", \"cardType\":").append(cardType)
                .append('}');
        return sb.toString();
    }
}
