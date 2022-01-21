package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.network.ResponseUtil;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.RoomManager;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.util.JsonUtil;
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
            DsMessage message = (DsMessage) data;
            GameInfoRequest request = (GameInfoRequest) JsonUtil.mapToObj(message.getData(), GameInfoRequest.class);
            String id = request.getUserId();
            Room room = RoomManager.findRoomById(id);
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

                for (Player player : players) {

                    if(!player.equals(p)) {
                        ChannelHandlerContext context = (ChannelHandlerContext)player.getContext();
                        context.channel().eventLoop().execute(() -> {
                            // 提交任务
                            // 封装一个 Response 对象
                            String userId = player.id();
                            GameInfoResponse r = new GameInfoResponse(userId, card, null);

                            context.writeAndFlush(
                                    ResponseUtil.response(
                                            DsMessage.build(EVENT_RECEIVE_OTHER_CARD_ID, userId, DsResult.data(r))));
                        });
                    }

                }

                return DsResult.ok();
            }

            throw new IllegalArgumentException("错误的出牌请求");

        });



    }
}
