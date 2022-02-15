package ds.guang.majing.common.cache;

import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.state.StateMachine;

import static ds.guang.majing.common.util.DsConstant.preRoomInfoPrev;
import static ds.guang.majing.common.util.DsConstant.preUserMachinekey;

/**
 * @author guangyong.deng
 * @date 2022-02-11 13:55
 */
public class CacheUtil {

    public static Room getRoomById(String id) {
        return (Room) Cache.getInstance().getObject(preRoomInfoPrev(id));
    }


    public static GameUser getGameUser(String username) {
       return  (GameUser) Cache.getInstance().getObject("guangmingdexin");
    }


    public static String getUserId() {
        GameUser gameUser = (GameUser) Cache.getInstance().getObject("guangmingdexin");
        if(gameUser == null) {
            throw new NullPointerException("gameUser is null");
        }
        return gameUser.getUserId();
    }


    public static StateMachine getStateMachine() {
        return (StateMachine) Cache.getInstance().getObject(preUserMachinekey("machine-1"));
    }
}
