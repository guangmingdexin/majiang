package ds.guang.majing.common.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ds.guang.majing.common.dto.GameUser;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author asus
 */
public class ServerPlayer extends Player  {

    @JsonIgnore
    private transient ChannelHandlerContext context;

    public ServerPlayer() {
    }

    public ServerPlayer(GameUser gameUser) {
        super(gameUser);
    }

    public ServerPlayer setContext(ChannelHandlerContext context) {
        this.context = context;
        return this;
    }


    @Override
    public Object getContext() {
        return context;
    }

    @Override
    public Player convertTo() {
        // 对象复制
        return new ClientPlayer()
                .setGameUser(getGameUser())
                .setCards(getCards());
    }
}
