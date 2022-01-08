package ds.guang.majing.common.player;

import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.player.Player;

import java.util.List;

/**
 * @author guangyong.deng
 * @date 2022-01-07 16:26
 */
public class ClientPlayer implements Player {

    private GameUser gameUser;

    @Override
    public List<Integer> getCards() {
        return null;
    }

    @Override
    public boolean addCard(int cardNum) {
        return false;
    }

    @Override
    public boolean removeCard(int cardIndex) {
        return false;
    }

    @Override
    public boolean remove(int cardNum) {
        return false;
    }

    @Override
    public GameUser getGameUserInfo() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Object getContent() {
        return null;
    }
}
