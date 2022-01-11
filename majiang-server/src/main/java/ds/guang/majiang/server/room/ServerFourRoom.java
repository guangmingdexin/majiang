package ds.guang.majiang.server.room;

import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author guangmingdexin
 */
public class ServerFourRoom extends Room implements Serializable {


    public ServerFourRoom() {}

    public ServerFourRoom(int playerCount, int initialCardNum, Player[] players) {
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
