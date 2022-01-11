package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.network.ResponseUtil;
import ds.guang.majiang.server.room.RoomManager;
import ds.guang.majing.common.ClassUtil;
import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;
import ds.guang.majing.common.state.State;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

import static ds.guang.majing.common.DsConstant.*;

/**
 * @author guangmingdexin
 */
@StateMatchAction(value = STATE_PREPARE_ID)
public class PrepareAction implements Action {


    @Override
    @SuppressWarnings("unchecked")
    public void handler(State state) {

        state.onEntry(data -> {
            // TODO 好像出了线程问题！
            // System.out.println("进入游戏准备 state!" + state);
            return null;
        });

        state.onEvent(EVENT_GET_HANDCARD_ID, data -> {

            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage message = (DsMessage) data;

            String id = message.getData().toString();
            Room room = getRoomById(id);
            Player player = room.findPlayerById(id);

            // 获取通道
            ChannelHandlerContext context = (ChannelHandlerContext) player.getContext();

//            System.out.println("eventLoop: " + context.channel().eventLoop().toString());
//            System.out.println("thread-name: " + Thread.currentThread().getName());

            context.channel().eventLoop().execute(() -> {
                List<Integer> cards = player.getCards();
                if(cards == null) {
                    // 说明此时玩家还未进行分配
                    room.assignCardToPlayer();
                    cards = player.getCards();
                }
                System.out.println("send cards: " + cards);
                message.setData(DsResult.data(cards));
                context.writeAndFlush(ResponseUtil.response(message));
            });

            return DsResult.ok();
        });

    }


    /**
     * @param id 玩家 id
     * @return
     */
    private Room getRoomById(String id) {

        // 获取房间管理器
        RoomManager roomManager = RoomManager.getInstance();
        return roomManager.get(preRoomInfoPrev(id));
    }



}
