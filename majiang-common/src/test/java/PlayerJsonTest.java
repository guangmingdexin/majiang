import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.CardType;
import ds.guang.majing.common.game.card.MaJiang;
import ds.guang.majing.common.util.JsonUtil;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.player.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author guangyong.deng
 * @date 2022-01-07 17:25
 */
public class PlayerJsonTest {


    public static void main(String[] args) {

      //  JsonUtil.getMapper().configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        GameUser gameUser = new GameUser();

        gameUser.setScore(1).setUserId(UUID.randomUUID().toString().substring(1, 8)).setUsername("gm");
        ServerPlayer player = new ServerPlayer(gameUser);

        Map<Card, Integer> map = new HashMap<>();

        map.put(new MaJiang(108, CardType.generate(108)), 1);

        player.setEventCard(map);

        String o = JsonUtil.objToJson(player);
        System.out.println(o);

        System.out.println(JsonUtil.stringToObj(o, ServerPlayer.class));


    }
}
