import com.fasterxml.jackson.databind.DeserializationFeature;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.player.ServerPlayer;

import java.util.UUID;

/**
 * @author guangyong.deng
 * @date 2022-01-07 17:25
 */
public class PlayerJsonTest {


    public static void main(String[] args) {

        JsonUtil.getMapper().configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        GameUser gameUser = new GameUser();

        gameUser.setScore(1).setUserId(UUID.randomUUID().toString().substring(1, 8)).setUsername("gm");
        ServerPlayer player = new ServerPlayer(gameUser);

        String json = player.toString();
        System.out.println(json);

        String o = JsonUtil.objToJson(player);
        System.out.println(o);
        System.out.println(JsonUtil.stringToObj(o, ServerPlayer.class));


    }
}
