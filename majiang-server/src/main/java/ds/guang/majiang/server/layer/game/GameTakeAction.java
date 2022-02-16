package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.machines.StateMachines;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.ServerFourRoom;
import ds.guang.majing.common.util.ResponseUtil;
import ds.guang.majing.common.game.card.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * 游戏摸牌逻辑处理状态，包括
 * @author asus
 */
@StateMatchAction(value = STATE_TAKE_CARD_ID)
public class GameTakeAction implements Action {

    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            System.out.println("进入摸牌状态！" + data);
            return data;
        });

        // 如果没有其他事件发生，则玩家正常进入下一个阶段
        // 如果玩家有可选事件发生，则玩家进入事件阶段，处理事件
        state.onEvent(EVENT_TAKE_CARD_ID, data -> {

            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;
            GameInfoRequest request = message.getData();
            String id = request.getUserId();

            // 1.获取房间信息，判断是否为当前玩家，如果是，则返回棋牌信息，包括判断是否有特殊事件
            ServerFourRoom room = ServerFourRoom.getRoomById(id);

            if(room.isCurAround(id) && room.check(id)) {

                // 从棋牌中，获取一张牌，放入玩家手牌中，并开始判断事件
                int markIndex = room.getMarkIndex();
                // 返回信息
                GameInfoResponse info;
                // 获取当前回合玩家
                Player p = room.findPlayerById(id);
                StateMachine<String, String, DsResult> machine = StateMachines
                        .get(preUserMachinekey(id));

                // 判断是否还有手牌可以摸
                if(markIndex < room.getMaxCardNum()) {
                    Integer take = room.getInitialCards().get(markIndex);
                    // 移动初始手牌，到下一张
                    room.setMarkIndex(markIndex + 1);
                    // 加入玩家手牌
                    p.addCard(take);
                    // 判断事件
                    // 麻将棋牌
                    Card majiang = new MaJiang(take, CardType.generate(take));
                    GameEvent event = p.event(majiang, EVENT_TAKE_CARD_ID, id);

                    info = new GameInfoResponse()
                            .setUserId(id)
                            .setCard(majiang)
                            .setEvent(event);

                    // 状态转换，根据条件，如果没有特殊事件，则正常进入出牌状态
                    // 否则进入特殊事件状态，等待玩家响应之后，进入下一个状态

                    if(event == null){
                        machine.setCurrentState(STATE_TAKE_OUT_CARD_ID, null);
                    }else {
                        System.out.println("event: " + event);
                        machine.setCurrentState(STATE_EVENT_ID, null);
                    }


                    // 发送消息给玩家
                    ChannelHandlerContext context = (ChannelHandlerContext)p.getContext();
                    // 返回结果

                    context.channel().eventLoop().execute(() -> {
                        DsMessage<GameInfoResponse> respMessage = DsMessage.build(
                                message.getServiceNo(),
                                message.getRequestNo(),
                                DsResult.data(info)
                        );
                        context.writeAndFlush(ResponseUtil.response(respMessage));
                    });

                }else {
                    room.getEventHandler().over(room);
                }
                return DsResult.ok();
            }
            // 2.如果不是当前玩家，直接抛出异常
            throw new IllegalArgumentException("state is error!");
        });
    }
}
