package ds.guang.majing.common.game.message;


import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEvent;

import java.io.Serializable;

/**
 * 返回的游戏包信息
 * @author asus
 */
public class GameInfoResponse implements Serializable {

    /**
     * 棋牌信息
     */
    private Card card;


    /**
     * 游戏事件信息
     */
    private GameEvent event;

    public GameInfoResponse(Card card, GameEvent event) {
        this.card = card;
        this.event = event;
    }

    public Card getCard() {
        return card;
    }

    public GameInfoResponse setCard(Card card) {
        this.card = card;
        return this;
    }

    public GameEvent getEvent() {
        return event;
    }

    public GameInfoResponse setEvent(GameEvent event) {
        this.event = event;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"card\":").append(card)
                .append(", \"event\":").append(event)
                .append('}');
        return sb.toString();
    }
}
