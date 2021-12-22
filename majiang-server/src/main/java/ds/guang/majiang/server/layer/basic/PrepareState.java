package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.room.FourRoom;
import ds.guang.majing.common.player.Player;
import ds.guang.majiang.server.pool.MatchPool;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.room.Room;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author guangmingdexin
 */
public class PrepareState extends AbstractStateImpl<String, String, DsResult> {


    private MatchPool matchPool;

    private static final int PLAYER_COUNT = 4;

    /**
     * @param id 状态ID
     */
    public PrepareState(String id) {
        super(id);
    }

    @Override
    public State<String, String, DsResult> onEvent(String eventId, String nextState) {
        // 1.获取游戏玩家 id，加入游戏池中，如果游戏池中存在四位玩家则直接组成一个房间
        // 如果没有匹配好，直接阻塞？
        Future<List<Player>> matchResult = matchPool.match();
        List<Player> players = null;
        try {
            players = matchResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // 判断 players 是否匹配成功！
        if(matchResult.isDone() && players != null && players.size() >= PLAYER_COUNT) {

            List<Player> finalPlayers = players;
            return onEvent(eventId, nextState, data -> {
                Room room = new FourRoom(PLAYER_COUNT, finalPlayers);
                return DsResult.data(room);
            });
        }
        return this;
    }

    @Override
    public State<String, String, DsResult> onEntry(Handler handler) {
        System.out.println("进入 prepare 状态！");
        return super.onEntry(handler);
    }
}
