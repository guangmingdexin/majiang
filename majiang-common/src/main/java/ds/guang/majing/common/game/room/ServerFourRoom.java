package ds.guang.majing.common.game.room;

import ds.guang.majing.common.game.player.Player;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * @author guangmingdexin
 */
public class ServerFourRoom extends Room implements Serializable {


    public ServerFourRoom() {}

    @Override
    public boolean isCurAround(String userId) {
        Player player = super.players[curRoundIndex % playerCount];
        return player != null && player.id().equals(userId);
    }

    @Override
    public boolean check(String userId) {
        // 1.牌数不能超过 14 ，必须至少为 1
        // 2.牌数必须为奇数
        // 3.同一个数字不能出现超过 4 次
        Player p = this.findPlayerById(userId);
        List<Integer> cards = p.getCards();

        if(cards == null) {
            throw new NullPointerException("手牌为空");
        }

        int size = cards.size();
        if(size > maxCardNum && size < minCardNum)  {
            throw new IllegalArgumentException("play cards is error！");
        }

        if((size & (size - 1)) == 0) {
            throw  new IllegalArgumentException("play cards is error！");
        }
        return true;
    }



    public ServerFourRoom(int playerCount,
                          int initialCardNum,
                          int maxCardNum,
                          int minCardNum,
                          Player[] players) {
        super();
        super.id = UUID.randomUUID().toString().substring(0, 6);
        super.playerCount = playerCount;
        super.players = players;
        super.initialCardNum = initialCardNum;

        if(getInitialCards() == null || getInitialCards().isEmpty()) {
            // 还未进行初始化
            setInitialCards(shuffle(Room.CARDS));
        }
    }
}
