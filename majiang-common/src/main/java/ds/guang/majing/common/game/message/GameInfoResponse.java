package ds.guang.majing.common.game.message;


import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.MaJiangEvent;


import java.io.Serializable;
import java.util.Map;

/**
 * 返回的游戏包信息
 * @author asus
 */
public class GameInfoResponse implements Serializable {

    private String userId;

    /**
     * 棋牌信息
     */
    private Card card;


    /**
     * 游戏事件信息
     */
    private Map<MaJiangEvent, Integer> event;

    public GameInfoResponse() {
    }

    public GameInfoResponse(String userId, Card card, Map<MaJiangEvent, Integer> event) {
        this.userId = userId;
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

    public Map<MaJiangEvent, Integer> getEvent() {
        return event;
    }

    public GameInfoResponse setEvent(Map<MaJiangEvent, Integer> event) {
        this.event = event;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public GameInfoResponse setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"userId\":").append(userId)
                .append(", \"card\":").append(card)
                .append(", \"event\":").append(event)
                .append('}');
        return sb.toString();
    }
}
