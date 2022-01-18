package ds.guang.majing.common.game.player;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ds.guang.majing.common.util.Algorithm;
import ds.guang.majing.common.util.Converter;
import ds.guang.majing.common.game.dto.GameUser;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家抽象接口
 *
 * @author guangyong.deng
 * @date 2021-12-17 14:05
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServerPlayer.class, name = "serverPlayer"),
        @JsonSubTypes.Type(value = ClientPlayer.class, name = "clientPlayer")
})
public abstract class Player implements Cloneable, Serializable {

    private GameUser gameUser;

    @JsonSerialize(converter = Converter.class)
    private List<Integer> cards;

    public Player() {
    }

    public Player(GameUser gameUser) {
        this.gameUser = gameUser;
    }

    /**
     * 获取玩家手牌
     *
     * @return
     */
    public List<Integer> getCards() {
        return cards;
    };

    public Player setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    /**
     *
     * 加入手牌
     *
     * @param cardNum 手牌
     * @return 添加是否成功
     */
    public boolean addCard(int cardNum) {

        // 这里插入，必须保证手牌的有序性
        int index = Algorithm.binarySearch(cards, cardNum);
        if(index < 0) {
            throw new IllegalArgumentException("插入算法出现问题！");
        }
        cards.set(index, cardNum);

        return true;
    }

    /**
     *
     * 移除下标为 cardIndex 的手牌
     *
     * @param cardIndex
     * @return
     */
    public boolean removeCard(int cardIndex) {
        return false;
    }

    /**
     *
     * 移除点数为 cardNum 的手牌
     *
     * @param cardNum 手牌
     * @return
     */
    public boolean remove(int cardNum) {
        return false;
    }



    /**
     * 玩家 id
     * @return id
     */
    public String id() {
        return gameUser.getUserId();
    }


    public GameUser getGameUser() {
        return gameUser;
    }

    public Player setGameUser(GameUser gameUser) {
        this.gameUser = gameUser;
        return this;
    }

    /**
     * 返回 网络通道
     * @return 通道
     */
    public abstract Object getContext();


    /**
     * 不同类型的 Player 之间的转换
     * @return
     */
    public abstract Player convertTo();


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"gameUser\":").append(gameUser)
                .append(", \"cards\":").append(cards)
                .append('}');
        return sb.toString();
    }
}
