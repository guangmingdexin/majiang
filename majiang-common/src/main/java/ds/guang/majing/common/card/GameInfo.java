package ds.guang.majing.common.card;


import java.io.Serializable;

/**
 * 返回的游戏包信息
 * @author asus
 */
public class GameInfo implements Serializable {

    /**
     * 棋牌信息
     */
    private Card card;


    /**
     * 游戏事件信息
     */
    private GameEvent event;

    public GameInfo(Card card, GameEvent event) {
        this.card = card;
        this.event = event;
    }

    public Card getCard() {
        return card;
    }

    public GameInfo setCard(Card card) {
        this.card = card;
        return this;
    }

    public GameEvent getEvent() {
        return event;
    }

    public GameInfo setEvent(GameEvent event) {
        this.event = event;
        return this;
    }
}
