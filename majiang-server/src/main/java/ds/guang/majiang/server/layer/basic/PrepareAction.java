package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.machines.StateMachines;
import ds.guang.majing.common.game.room.ServerFourRoom;
import ds.guang.majing.common.util.ResponseUtil;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

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

        state.onEvent(EVENT_HANDCARD_ID,  data -> {
            // data 必须为 Message<GameInfoRequest>
            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;

            // 获取游戏包
            GameInfoRequest request = message.getData();

            String id = request.getUserId();
            // 获取房间
            ServerFourRoom room = ServerFourRoom.getRoomById(id);
            // 获取玩家
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
                // 构造回复消息包
                GameInfoResponse infoResponse = new GameInfoResponse().setCards(cards);

                DsMessage<DsResult<GameInfoResponse>> respMessage = DsMessage.build(message.getServiceNo(),
                        message.getRequestNo(), DsResult.data(infoResponse));

//                System.out.println("分配手牌：" + cards + " id: " + id);
                context.writeAndFlush(ResponseUtil.response(respMessage));
            });

            // 根据是否为自身回合，到达下一状态
            StateMachine<String, String, DsResult> stateMachine = StateMachines.get(preUserMachinekey(id));
            if(room.isCurAround(id)) {
                // 进入摸牌状态
                stateMachine.setCurrentState(STATE_TAKE_CARD_ID, data);
            }else {
                // 进入等待状态
                stateMachine.setCurrentState(STATE_WAIT_ID, data);
            }

            return DsResult.ok();
        });

    }
}
