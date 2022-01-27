package ds.guang.majing.common.game.message;


import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.MaGameEvent;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.room.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 返回的游戏包信息
 * @author asus
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class GameInfoResponse implements Serializable {


    private String serviceName;

    /**
     * 请求序号
     */
    private String requestNo;

    /**
     * 玩家 id
     */
    private String userId;

    /**
     * 棋牌信息
     */
    private Card card;


    /**
     * 游戏事件信息
     */
    private GameEvent event;

    /**
     * 游戏事件状态
     */
    private String eventStatus;

    /**
     * 手牌信息
     */
    private List<Integer> cards;


    /**
     * 房间信息
     */
    private Room room;

    /**
     * 玩家信息
     */
    private GameUser gameUser;


    /**
     * 当前玩家回合
     */
    private int curRoundIndex;



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"requestNo\":").append(requestNo)
                .append(", \"userId\":").append(userId)
                .append(", \"card\":").append(card)
                .append(", \"event\":").append(event)
                .append(", \"eventStatus\":").append(eventStatus)
                .append(", \"cards\":").append(cards)
                .append(", \"room\":").append(room)
                .append(", \"gameUser\":").append(gameUser)
                .append(", \"curRoundIndex\":").append(curRoundIndex)
                .append('}');
        return sb.toString();
    }
}
