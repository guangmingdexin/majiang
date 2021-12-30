package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.room.FourRoom;
import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.player.Player;
import ds.guang.majiang.server.pool.MatchPool;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.room.Room;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static ds.guang.majing.common.DsConstant.EVENT_PREPARE_ID;
import static ds.guang.majing.common.DsConstant.STATE_INITIAL_ID;
import static ds.guang.majing.common.DsConstant.STATE_PREPARE_ID;

/**
 * @author guangmingdexin
 */
@StateMatchAction(value = STATE_PREPARE_ID)
public class PrepareAction implements Action {

    private MatchPool matchPool;

    private static final int PLAYER_COUNT = 4;


    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            System.out.println("进入 prepare 状态！");
            return null;
        });

        state.onEvent(EVENT_PREPARE_ID, STATE_INITIAL_ID, data -> {

            // 1.获取游戏玩家 id，加入游戏池中，如果游戏池中存在四位玩家则直接组成一个房间
            // 如果没有匹配好，直接阻塞？
            System.out.println("data: " + data);
            //MpscArrayQueue
            // 封装一个异步任务去执行，免得阻塞 NioEventLoop
            CompletableFuture.runAsync(() -> {
                // 开始匹配
               // matchPool.addPlayer()
                matchPool.match();
            });

            List<Player> players = null;

            // 判断 players 是否匹配成功！
            if(players != null && players.size() >= PLAYER_COUNT) {
                Room room = new FourRoom(PLAYER_COUNT, players);
                return DsResult.data(room);
            }

           // return DsResult.error("游戏匹配失败！");
            return DsResult.empty("正在匹配中！");
        });
    }
}
