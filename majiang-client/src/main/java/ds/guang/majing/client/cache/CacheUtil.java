package ds.guang.majing.client.cache;

import ds.guang.majing.client.game.ClientFourRoom;
import ds.guang.majing.client.game.ClientPlayer;
import ds.guang.majing.client.remote.dto.vo.LoginVo;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.player.ServerPlayer;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.state.StateMachine;

import java.util.ArrayList;
import java.util.Arrays;

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
            // TODO: 测试阶段
          //  throw new NullPointerException("请先登录！");
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

    public static ClientFourRoom room;

    static {

       room = new ClientFourRoom();

        room.setCurRoundIndex(0);
        room.setPlayerCount(2);
        room.setPlayers(new Player[]{new ClientPlayer()
                .setGameUser(new GameUser().setUserId("NULL"))
                .setCards(Arrays.asList(11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11))});
    }

    public static ClientFourRoom getRoom() {

        return room;

       // return (ClientFourRoom) Cache.getInstance().getObject(preRoomInfoPrev(getUserId()));
    }


    public static ClientPlayer getPlayer() {
        return (ClientPlayer) getRoom().findPlayerById(CacheUtil.getUserId());
    }
}
