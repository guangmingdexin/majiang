package ds.guang.majing.common.player;

import ds.guang.majing.common.dto.GameUser;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家抽象接口
 *
 * @author guangyong.deng
 * @date 2021-12-17 14:05
 */
public interface Player extends Cloneable, Serializable {

    /**
     * 获取玩家手牌
     *
     * @return
     */
    List<Integer> getCards();


    /**
     *
     * 加入手牌
     *
     * @param cardNum
     * @return
     */
    boolean addCard(int cardNum);

    /**
     *
     * 移除下标为 cardIndex 的手牌
     *
     * @param cardIndex
     * @return
     */
    boolean removeCard(int cardIndex);

    /**
     *
     * 移除点数为 cardNum 的手牌
     *
     * @param cardNum 手牌
     * @return
     */
    boolean remove(int cardNum);

    /**
     * 获取游戏玩家信息
     * @return gameUser
     */
    GameUser getGameUserInfo();

    /**
     * 玩家 id
     * @return id
     */
    String getId();

}
