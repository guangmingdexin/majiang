package ds.guang.majiang.server.room;

import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;

import java.util.List;

/**
 * @author guangmingdexin
 */
public class FourRoom extends Room {

    public FourRoom(int playerCount, List<Player> players) {
        super.playerCount = playerCount;
        super.players = players;
    }
}
