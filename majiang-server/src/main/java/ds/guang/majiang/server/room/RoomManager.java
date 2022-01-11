package ds.guang.majiang.server.room;

import ds.guang.majing.common.room.Room;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
