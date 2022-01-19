package ds.guang.majing.common.game.room;

import ds.guang.majing.common.game.player.Player;

/**
 * @author guangyong.deng
 * @date 2022-01-07 16:21
 */
public class ClientFourRoom extends Room {


    @Override
    public boolean isCurAround(String userId) {
        // 1.判断 curIndex 下标是否为 存在玩家
        Player p = super.players[curRoundIndex];
        return p != null && p.id().equals(userId);
    }

    @Override
    public boolean check(String userId) {
       return true;
    }


}
