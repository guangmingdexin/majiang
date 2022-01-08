package ds.guang.majing.common.player;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ds.guang.majing.common.Converter;
import ds.guang.majing.common.dto.GameUser;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 *
 * @author asus
 */
public class ServerPlayer implements Player  {

    private GameUser gameUser;


    @JsonSerialize(converter = Converter.class)
    private List<Integer> cards;


    private transient ChannelHandlerContext context;

    public ServerPlayer() { }

    public ServerPlayer(GameUser gameUser) {
        this.gameUser = gameUser;
    }

    public ServerPlayer setContext(ChannelHandlerContext context) {
        this.context = context;
        return this;
    }

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
        return gameUser;
    }

    public GameUser getGameUser() {
        return gameUser;
    }

    public ServerPlayer setGameUser(GameUser gameUser) {
        this.gameUser = gameUser;
        return this;
    }

    @Override
    public String getId() {
        return gameUser.getUserId();
    }

    @Override
    public Object getContent() {
        return context;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"gameUser\":").append(gameUser.toString())
                .append('}');
        return sb.toString();
    }
}
