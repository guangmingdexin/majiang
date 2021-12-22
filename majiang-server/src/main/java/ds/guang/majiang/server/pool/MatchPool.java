package ds.guang.majiang.server.pool;

import ds.guang.majing.common.player.Player;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author guangmingdexin
 */
public interface MatchPool {



    /**
     * 游戏服务器一旦开始启动则需要开启游戏匹配
     */
    void start();


    /**
     * 获取游戏池运行状态
     * @return boolean
     */
    boolean isValid();

    /**
     * 游戏池是否启动
     * @return
     */
    boolean isStart();

    /**
     *
     * 将玩家加入游戏池
     *
     * @param player 玩家
     * @return boolean
     */
    boolean addPlayer(Player player);


    /**
     * 移除玩家
     * 如果该玩家不在游戏池中，同样返回 true
     *
     * @param player 玩家
     * @return
     */
    boolean removePlayer(Player player);


    /**
     *
     * 返回所有匹配成功的玩家
     *
     * @return 匹配成功的玩家
     */
   Future<List<Player>> match();
}
