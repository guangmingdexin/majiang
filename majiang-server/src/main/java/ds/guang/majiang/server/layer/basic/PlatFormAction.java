package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.player.ServerPlayer;
import ds.guang.majiang.server.pool.MatchPool;
import ds.guang.majing.common.util.ClassUtil;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.util.JsonUtil;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.state.State;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 *
 * 登陆之后的下一个状态
 *
 * @author asus
 */
@StateMatchAction(value = STATE_PLATFORM_ID)
public class PlatFormAction implements Action {

    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {

        state.onEvent(EVENT_PREPARE_ID, STATE_PREPARE_ID, data -> {

            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;
            // 这里还需要将 data 重新反序列化
            GameInfoRequest request = message.getData();
            String userId = request.getUserId();
            // 开启匹配池
            MatchPool matchPool = MatchPool.getInstance();
            matchPool.start();
            // 1.获取游戏玩家 id
            // 如果没有匹配好，直接阻塞？
            // 2.通过 id, 查找缓存 玩家信息
            // 3.如果缓存不存在，则调用远程/本地服务 查找玩家信息，并存入缓存
            // 4.全局注入
            Cache cache = Cache.getInstance();
            Object gameUser =  cache.getObject(preGameUserInfoKey(userId));

            if(gameUser == null) {
                // TODO 未处理
            }

            if(message.getAttrMap() != null && message.getAttrMap().containsKey(SYS_CONTEXT)) {
                ChannelHandlerContext context = (ChannelHandlerContext) message.getAttrMap().get(SYS_CONTEXT);
                CompletableFuture.runAsync(() -> {
                    Player player = new ServerPlayer((GameUser) gameUser).setContext(context);
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
