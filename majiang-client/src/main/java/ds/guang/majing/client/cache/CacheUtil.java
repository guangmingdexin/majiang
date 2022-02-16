package ds.guang.majing.client.cache;

import ds.guang.majing.client.remote.dto.vo.LoginVo;
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


    public static GameUser getGameUser() {
        GameUser object = (GameUser) Cache.getInstance().getObject("User-Session:");
        if(object == null) {
            throw new NullPointerException("请先登录！");
        }
        return object;
    }


    public static String getUserId() {
        LoginVo loginVo = (LoginVo) Cache.getInstance().getObject("User-Token:");
        if(loginVo == null) {
//            throw new NullPointerException("请先登录！");
            return "NULL";
        }
        return loginVo.getUid();
    }

    public static LoginVo getToken() {

        return (LoginVo) Cache.getInstance().getObject("User-Token:");
    }

    public static StateMachine getStateMachine() {
        return (StateMachine) Cache.getInstance().getObject(preUserMachinekey("machine-1"));
    }

    public static Room getRoom(String id) {
        return (Room) Cache.getInstance().getObject(preRoomInfoPrev(id));
    }
}
