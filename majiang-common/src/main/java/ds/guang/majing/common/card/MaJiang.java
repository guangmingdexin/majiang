package ds.guang.majing.common.card;

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

    private String name;

    private CardType cardType;
}
