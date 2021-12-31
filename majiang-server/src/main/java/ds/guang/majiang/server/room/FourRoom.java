package ds.guang.majiang.server.room;

import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;

import java.util.List;
import java.util.UUID;

/**
 * @author guangmingdexin
 */
public class FourRoom extends Room {

    public FourRoom(int playerCount, Player[] players) {
        super();
        super.id = UUID.randomUUID().toString().substring(0, 6);
        super.playerCount = playerCount;
        super.players = players;
    }
}
