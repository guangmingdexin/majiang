package ds.guang.majing.common.room;

import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;

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
        return super.players[curRoundIndex % playerCount].id().equals(userId);
    }

    @Override
    public boolean check(String userId) {
        // 1.牌数不能超过 14 ，必须至少为 1
        // 2.牌数必须为奇数
        // 3.同一个数字不能出现超过 4 次
        Player p = this.findPlayerById(userId);
        List<Integer> cards = p.getCards();

        if(cards == null) {
            throw new NullPointerException("");
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

    @Override
    public int checkEvent(List<Integer> cards) {

        return 0;
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
