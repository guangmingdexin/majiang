package ds.guang.majiang.server.player;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ds.guang.majing.common.Converter;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.player.Player;

import java.util.List;

/**
 *
 * @author asus
 */
public class ServerPlayer implements Player  {

    private GameUser gameUser;

    @JsonSerialize(converter = Converter.class)
    private List<Integer> cards;

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


}