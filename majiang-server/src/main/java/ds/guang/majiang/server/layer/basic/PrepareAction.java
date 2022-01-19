package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.network.ResponseUtil;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.state.State;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangmingdexin
 */
@StateMatchAction(value = STATE_PREPARE_ID)
public class PrepareAction implements Action {

    private final Object lock = new Object();


    @Override
    @SuppressWarnings("unchecked")
    public void handler(State state) {

        state.onEntry(data -> {
            return null;
        });

        state.onEvent(EVENT_HANDCARD_ID, STATE_TAKE_CARD_ID, data -> {

            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage message = (DsMessage) data;

            String id = message.getData().toString();
            Room room = Room.getRoomById(id);
            Player player = room.findPlayerById(id);


            // 获取通道
            ChannelHandlerContext context = (ChannelHandlerContext) player.getContext();

            context.channel().eventLoop().execute(() -> {

                List<Integer> cards = player.getCards();
                // 发送多线程问题：多个玩家应该保证一次初始化
                // 这里可以不用 while 因为保证一个线程操作成功即可

                synchronized (lock) {
                    if(cards == null) {
                        // 说明此时玩家还未进行分配
                       // System.out.println(Thread.currentThread().getName() + " ---- 初始化！" + lock);
                        room.assignCardToPlayer();
                        cards = player.getCards();
                    }
                }
                message.setData(DsResult.data(cards));
//                System.out.println("分配手牌：" + cards + " id: " + id);
                context.writeAndFlush(ResponseUtil.response(message));
            });

            return DsResult.ok();
        });

    }
}
