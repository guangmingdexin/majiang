package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.pool.MatchPool;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.player.ServerPlayer;
import ds.guang.majing.common.state.State;
import ds.guang.majiang.server.game.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CompletableFuture;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2022-02-16 15:32
 */
@StateMatchAction(STATE_START_ID)
public class StartAction implements Action {


    @Override
    public void handler(State state) {


        state.onEvent(EVENT_START_ID, data -> {

            System.out.println("data: " + data);
            // 1.获取 context
            DsMessage message = (DsMessage) data;

            ChannelHandlerContext context = (ChannelHandlerContext)message.getAttrMap().get(SYS_CONTEXT);

            context.writeAndFlush(ResponseUtil.response(message));

            return DsResult.ok();
        });

        state.onEvent(EVENT_RANDOM_MATCH_ID, STATE_PREPARE_ID, data -> {

            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;
            // 这里还需要将 data 重新反序列化
            GameInfoRequest request = message.getData();

            // 开启匹配池
            MatchPool matchPool = MatchPool.getInstance();
            matchPool.start();
            // 1.获取游戏玩家 id
            // 如果没有匹配好，直接阻塞？
            GameUser gameUser = request.getGameUser();

            if(message.getAttrMap() != null && message.getAttrMap().containsKey(SYS_CONTEXT)) {
                CompletableFuture.runAsync(() -> {
                    Player player = new ServerPlayer(gameUser).setContext((ChannelHandlerContext)message.getAttrMap().get(SYS_CONTEXT));
                    matchPool.addPlayer(player);
                    matchPool.match();
                });
            }else {
                throw new NullPointerException("context is null!");
            }

            return DsResult.wait("游戏匹配中！");
        });

        state.onEvent(EVENT_MATCH_FRIEND_ID, STATE_MATCH_FRIEND_ID);
    }
}
