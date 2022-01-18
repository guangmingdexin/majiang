package ds.guang.majing.common.game.message;

import ds.guang.majing.common.game.card.Card;

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
    private Card takeOut;


    public String getUserId() {
        return userId;
    }

    public GameInfoRequest setUserId(String userId) {
        this.userId = userId;
        return this;
    }


}
