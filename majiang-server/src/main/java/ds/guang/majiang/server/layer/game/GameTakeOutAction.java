package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majing.common.game.card.GameEventHandler;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.util.ResponseUtil;
import ds.guang.majing.common.game.card.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.RoomManager;
import ds.guang.majing.common.state.State;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2022-01-18 17:27
 */
@StateMatchAction(value = STATE_TAKE_OUT_CARD_ID)
public class GameTakeOutAction implements Action {


    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {

        state.onEntry(data -> {
            System.out.println("进入出牌状态！");
            return data;
        });

        state.onEvent(EVENT_TAKE_OUT_CARD_ID, STATE_WAIT_ID, data -> {

            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;
            GameInfoRequest request = message.getData();

            String id = request.getUserId();
            Room room = Room.getRoomById(id);
            Player p = room.findPlayerById(id);
            Card card = request.getCard();
            Integer value = (Integer) card.value();

            // 1.校验是否为当前回合
            if(room.isCurAround(id)) {

                // 1.校验是否能够出这张牌 && 校验玩家手牌的合法性
                p.checkOut(value);
                room.check(id);

                // 2.移除手牌中的值
                p.remove(value);


                // 3.通知其他玩家
                Player[] players = room.getPlayers();

                GameEventHandler eventHandler = room.getEventHandler();

                for (Player player : players) {

                    if(!player.equals(p)) {
                        String userId = player.id();
                        // 判断其他玩家能不能够形成其他事件
                        GameEvent gameEvent = p.event(card, EVENT_RECEIVE_OTHER_CARD_ID, userId);
                        // 注意：这里是线程安全的
                        // 将玩家事件加入处理器中，稍后处理
                        eventHandler.addEvent(gameEvent);

                        ChannelHandlerContext context = (ChannelHandlerContext)player.getContext();
                        context.channel().eventLoop().execute(() -> {

                            // 提交任务
                            // 封装一个 Response 对象
                            GameInfoResponse r = new GameInfoResponse()
                                    .setUserId(userId)
                                    .setCard(card)
                                    .setServiceName(EVENT_RECEIVE_OTHER_CARD_ID);

                            context.writeAndFlush(
                                    ResponseUtil.response(
                                            DsMessage.build(EVENT_RECEIVE_OTHER_CARD_ID, userId, DsResult.data(r))));


                        });
                    }
                }

                if(eventHandler.isEmpty()) {
                    eventHandler.nextRound(MaJiangEvent.NOTHING.getValue() ,id, room);
                }

                return DsResult.ok();
            }
            throw new IllegalArgumentException("错误的出牌请求");

        });
    }
}
