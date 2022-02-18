package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majing.common.game.card.GameEventHandler;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.game.room.ServerFourRoom;
import ds.guang.majiang.server.game.ResponseUtil;
import ds.guang.majing.common.game.card.*;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.state.State;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

import static ds.guang.majing.common.game.card.MaJiangEvent.NOTHING;
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
            ServerFourRoom room = ServerFourRoom.getRoomById(id);
            Player p = room.findPlayerById(id);
            Card card = request.getCard();
            int value =  card.value();

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
                // 记录回合
                room.setPrevRoundIndex(room.getCurRoundIndex());
                for (Player player : players) {

                    if(!player.equals(p)) {
                        String userId = player.id();
                        // 判断其他玩家能不能够形成其他事件
                        GameEvent gameEvent = player.event(card, EVENT_RECEIVE_OTHER_CARD_ID, userId);
                        // 注意：这里是线程安全的
                        // 将玩家事件加入处理器中，稍后处理
                        eventHandler.addEvent(gameEvent);
                    }
                }
                int roundIndex = -1;
                if(eventHandler.isEmpty()) {
                    roundIndex = eventHandler.nextRound(
                            new MaGameEvent()
                                    .setPlayId(id)
                                    .setActionEvent(NOTHING),
                            room);
                }
                System.out.println("eventHandler: " + eventHandler);
                for (Player player : players) {

                    ChannelHandlerContext context = (ChannelHandlerContext)player.getContext();

                    // 提交任务
                    // 封装一个 Response 对象
                    String playId = player.id();
                    GameInfoResponse r = new GameInfoResponse()
                            .setUserId(playId)
                            .setCard(card)
                            .setServiceName(EVENT_RECEIVE_OTHER_CARD_ID)
                            .setCurRoundIndex(roundIndex)
                            .setPrevRoundIndex(room.getPrevRoundIndex());

                    DsMessage<DsResult<GameInfoResponse>> reply = DsMessage.build(
                            EVENT_RECEIVE_EVENT_REPLY_ID,
                            playId,
                            DsResult.data(r)
                    );
                    context.channel().eventLoop().execute(() -> {
                        ServerFourRoom.write(playId, ResponseUtil.response(reply));
                    });

                }
                return DsResult.ok();
            }
            throw new IllegalArgumentException("错误的出牌请求");

        });
    }
}
