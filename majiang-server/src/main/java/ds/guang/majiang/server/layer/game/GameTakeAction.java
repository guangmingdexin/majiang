package ds.guang.majiang.server.layer.game;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.Algorithm;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.card.*;
import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;
import ds.guang.majing.common.state.State;

import java.util.List;
import java.util.Objects;

import static ds.guang.majing.common.DsConstant.EVENT_POST_TAKE_CARD_ID;
import static ds.guang.majing.common.DsConstant.STATE_TAKE_CARD_ID;

/**
 * 游戏摸牌逻辑处理状态，包括
 * @author asus
 */
@StateMatchAction(value = EVENT_POST_TAKE_CARD_ID)
public class GameTakeAction implements Action {

    @Override
    public void handler(State state) {

        state.onEvent(EVENT_POST_TAKE_CARD_ID, STATE_TAKE_CARD_ID, data -> {

            Objects.requireNonNull(data, "data must be not empty!");
            DsMessage message = (DsMessage) data;
            String id = message.getData().toString();

            // 1.获取房间信息，判断是否为当前玩家，如果是，则返回棋牌信息，包括判断是否有特殊事件
            Room room = Room.getRoomById(id);

            if(room.isCurAround(id) && room.check(id)) {

                // 从棋牌中，获取一张牌，放入玩家手牌中，并开始判断事件
                int markIndex = room.getMarkIndex();
                Integer c = room.getInitialCards().get(markIndex);
                room.setMarkIndex(markIndex + 1);

                Player p = room.findPlayerById(id);
                List<Integer> cards = p.getCards();

                // 这里插入，必须保证手牌的有序性
                cards.set(Algorithm.binarySearch(cards, c), c);

                GameEvent gameEvent = new MaJiangEvent();

                if(Room.isGangEvent(cards, -1)) {
                    gameEvent.setEvent(MaJiangEvent.GANG_EVENT);
                }

                if(Room.isHuEvent(cards)) {
                    gameEvent.setEvent(MaJiangEvent.HU_EVENT);
                }

                Card majiang = new MaJiang(c, CardType.generate(c));
                GameInfo info = new GameInfo(majiang, gameEvent);

                return DsResult.data(info);

            }
            // 2.如果不是当前玩家，直接抛出异常
            throw new IllegalArgumentException("state is error!");
        });
    }
}
