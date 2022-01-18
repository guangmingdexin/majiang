package ds.guang.majing.client.entity;

import ds.guang.majing.common.game.room.Room;

import java.util.List;

/**
 * @author guangyong.deng
 * @date 2022-01-07 16:21
 */
public class ClientFourRoom extends Room {


    @Override
    public boolean isCurAround(String userId) {
        return false;
    }

    @Override
    public boolean check(String userId) {
        return false;
    }

    @Override
    public int checkEvent(List<Integer> cards) {
        return 0;
    }
}
