package ds.guang.majing.common.game.message;

import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.MaJiangEvent;

/**
 * @author asus
 */
public class GameInfoRequest {

    /**
     * 玩家 id
     */
    private String userId;


    /**
     * 出牌
     */
    private Card card;

    /**
     * 游戏事件
     */
    private MaJiangEvent event;

    public GameInfoRequest() {}

    public GameInfoRequest(String userId, MaJiangEvent event) {
        this.userId = userId;
        this.event = event;
    }

    public GameInfoRequest(String userId, Card card, MaJiangEvent event) {
        this.userId = userId;
        this.card = card;
        this.event = event;
    }

    public String getUserId() {
        return userId;
    }

    public Card getCard() {
        return card;
    }

    public MaJiangEvent getEvent() {
        return event;
    }
}
