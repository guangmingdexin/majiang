package ds.guang.majing.common.game.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.GameEventHandler;
import ds.guang.majing.common.game.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author asus
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServerFourRoom.class, name = "serverRoom")
})
@Getter
@Setter
public abstract class Room implements Serializable {

    /**
     * room Id
     */
    protected String id;

    /**
     * 玩家集合
     */
    protected Player[] players;

    /**
     * 当前使用的手牌下标
     */
    protected int markIndex;

    /**
     * 当前玩家回合下标（同位置相关，即该数字 % 4 可以对应到相应玩家，同时
     * 数组下标即为方位 比如 [A, B, C, D] -> [北，西，南，东]
     * ）
     */
    protected volatile int curRoundIndex;


    /**
     * 前一个出牌玩家的回合/方向，只有在玩家出牌之后更新
     */
    protected int prevRoundIndex;

    /**
     * 玩家人数
     */
    protected int playerCount;

    /**
     * 初始手牌数量
     */
    protected int initialCardNum;

    /**
     * 棋牌游戏中进行状态判断-最大手牌数量
     */
    protected int maxHandCardNum;

    /**
     * 最小手牌数量
     */
    protected int minHandCardNum;

    /**
     * 手牌池，所有手牌集合
     */
    @JsonIgnore
    protected transient List<Integer> initialCards;

    /**
     * 手牌池最大数量
     */
    protected int maxCardNum;

    /**
     * 游戏事件处理器
     */
    @JsonIgnore
    protected transient GameEventHandler eventHandler;

    public Player[] getPlayers() {
        return players;
    }

    /**
     * 本局游戏中胡牌人数
     */
    protected int countHu;

    public Room() {}

    /**
     *
     * 判断当前是否为当前玩家回合
     *
     * @param userId 用户 id
     * @return
     */
    public abstract boolean isCurAround(String userId);

    /**
     *
     * 对玩家手牌状态进行检查
     *
     * @param userId 用户 id
     * @return
     */
    public abstract boolean check(String userId);


    /**
     *
     * 房间事件处理
     *
     * @param event 游戏事件
     * @param cardNum 棋牌值
     */
    public abstract void eventHandler(GameEvent event, int cardNum);

    /**
     *
     * 根据用户 id 获取玩家信息
     *
     * @param userId
     * @return
     */
    public Player findPlayerById(String userId) {

        Objects.requireNonNull(userId, "userId must be not empty!");
        for (Player player : players) {
            if(userId.equals(player.id())) {
                return player;
            }
        }
        return null;
    }

    /**
     * 获取指定玩家的方位
     * @param userId 玩家 id
     * @return 玩家方位
     */
    public int direction(String userId) {

        for (int i = 0; i < players.length; i++) {
            if(players[i].id().equals(userId)) {
                return i;
            }
        }

        return -1;
    }

    /*************** 棋牌方法 ***********************/
    public boolean remove(String id, int cardNum) {
        Player p = findPlayerById(id);
        return p.remove(cardNum);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"id\":").append(id)
                .append(", \"players\":").append(Arrays.toString(players))
                .append(", \"markIndex\":").append(markIndex)
                .append(", \"curRoundIndex\":").append(curRoundIndex)
                .append(", \"playerCount\":").append(playerCount)
                .append(", \"cards\":").append(initialCards)
                .append('}');
        return sb.toString();
    }
}
