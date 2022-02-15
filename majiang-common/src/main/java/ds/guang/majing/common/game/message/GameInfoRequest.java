package ds.guang.majing.common.game.message;

import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.dto.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author asus
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class GameInfoRequest implements Serializable {


    /**
     * 消息请求序号，先暂时和 userId 一致，后期再更改
     */
    private String requestNo;

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
    private GameEvent event;

    /**
     * 用户信息
     */
    private User user;


    /**
     * 游戏用户，由客户端从第三方服务获取
     */
    private GameUser gameUser;


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"requestNo\":").append(requestNo)
                .append(", \"userId\":").append(userId)
                .append(", \"card\":").append(card)
                .append(", \"event\":").append(event)
                .append('}');
        return sb.toString();
    }
}
