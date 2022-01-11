package ds.guang.majing.common.player;

import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.player.Player;

import java.util.List;

/**
 * @author guangyong.deng
 * @date 2022-01-07 16:26
 */
public class ClientPlayer extends Player {


    public ClientPlayer() {
    }

    public ClientPlayer(GameUser gameUser) {
        super(gameUser);
    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Player convertTo() {
        return new ServerPlayer()
                .setGameUser(getGameUser())
                .setCards(getCards());
    }
}
