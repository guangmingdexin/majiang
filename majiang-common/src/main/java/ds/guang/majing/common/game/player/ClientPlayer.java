package ds.guang.majing.common.game.player;

import ds.guang.majing.common.game.dto.GameUser;

/**
 * @author guangyong.deng
 * @date 2022-01-07 16:26
 */
public class ClientPlayer extends Player {


    public ClientPlayer() {}

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

    @Override
    public void eventHandler(int eventValue, int cardNum) {

    }
}
