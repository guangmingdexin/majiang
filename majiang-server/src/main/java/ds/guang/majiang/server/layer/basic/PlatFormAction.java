package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.player.ServerPlayer;
import ds.guang.majiang.server.pool.MatchPool;
import ds.guang.majing.common.ClassUtil;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.state.State;

import java.util.concurrent.CompletableFuture;

import static ds.guang.majing.common.DsConstant.*;

/**
 *
 * 登陆之后的下一个状态
 *
 * @author asus
 */
@StateMatchAction(value = STATE_PLATFORM_ID)
public class PlatFormAction implements Action {

    private static final int PLAYER_COUNT = 2;

    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {

        state.onEntry(data -> {
           // System.out.println("进入平台！");
            return null;
        });

        state.onEvent(EVENT_PREPARE_ID, STATE_PREPARE_ID, data -> {

            DsMessage message = ClassUtil.convert(data, DsMessage.class);
            // 这里还需要将 data 重新反序列化
            String userId = (String) JsonUtil.mapToObj(message.getData(), String.class);

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

            CompletableFuture.runAsync(() -> {
                Player player = new ServerPlayer((GameUser) gameUser);
                matchPool.addPlayer(player);
                matchPool.match();
            });


                /**
                 *  EventLoop A,B
                 *
                 *  A 执行 #match，启动 Pool C 线程（同时可能竞争 阻塞队列--无影响）
                 *
                 *  此时接入 另一个客户端 ，启动线程 B (同时可能竞争 阻塞队列)
                 *
                 *  1. C 线程获取到锁， A，B 继续阻塞 ，执行循环，直到 B 线程取得锁，加入阻塞队列
                 *      C 此时陷入阻塞，然后 B 加入之后，释放锁，C 开始执行循环
                 *      异常情况#1 B 还未执行到 match 方法 C 就返回了，然后 B 执行 match
                 *      进入阻塞，但是 队列中的玩家已被移除，不满足条件一直阻塞
                 *
                 *
                 *  满足退出 C 线程的条件，返回 future
                 *
                 *  #result 现在线程 A 返回结束，B 继续被阻塞，且客户端都无数据收到
                 *
                 *
                 *
                 */
            System.out.println("游戏匹配中！");
            return DsResult.empty("游戏匹配中！");
        });


        state.onEvent(EVENT_MATCH_FRIEND_ID, STATE_MATCH_FRIEND_ID);
    }

}
