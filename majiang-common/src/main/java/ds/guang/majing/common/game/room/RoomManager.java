package ds.guang.majing.common.game.room;

import ds.guang.majing.common.util.DsConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ds.guang.majing.common.util.DsConstant.preRoomInfoPrev;

/**
 * @author guangmingdexin
 */
public class RoomManager {

    private volatile static RoomManager INSTANCE;

    /**
     * key
     */
    private Map<String, Room> roomManager;

    private RoomManager() {
        this.roomManager = new ConcurrentHashMap<>();
    }

    public Room put(String playerId, Room room) {
        return roomManager.put(playerId, room);
    }

    public Room get(String playerId) {
        return roomManager.get(playerId);
    }

    public static RoomManager getInstance() {
        if(INSTANCE == null) {
            synchronized(RoomManager.class) {
                if(INSTANCE == null) {
                    INSTANCE = new RoomManager();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"roomManager\":").append(roomManager)
                .append('}');
        return sb.toString();
    }
}
