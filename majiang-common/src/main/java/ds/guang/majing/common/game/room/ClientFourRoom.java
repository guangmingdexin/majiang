package ds.guang.majing.common.game.room;

import ds.guang.majing.common.cache.CacheUtil;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.state.StateMachine;
import lombok.NoArgsConstructor;

import static ds.guang.majing.common.game.card.MaJiangEvent.*;
import static ds.guang.majing.common.util.DsConstant.STATE_TAKE_CARD_ID;
import static ds.guang.majing.common.util.DsConstant.STATE_TAKE_OUT_CARD_ID;
import static ds.guang.majing.common.util.DsConstant.STATE_WAIT_ID;

/**
 * @author guangyong.deng
 * @date 2022-01-07 16:21
 */
@NoArgsConstructor
@SuppressWarnings("unchecked")
public class ClientFourRoom extends Room {


    @Override
    public boolean isCurAround(String userId) {
        // 1.判断 curIndex 下标是否为 存在玩家
        if(curRoundIndex < 0) {
            return false;
        }
        Player p = super.players[curRoundIndex % playerCount];
        return p != null && p.id().equals(userId);
    }

    @Override
    public boolean check(String userId) {
       return true;
    }

    @Override
    public void eventHandler(GameEvent event, int cardNum) {

        String playId = event.getPlayId();
        int eventValue = event.getEvent();

        Player p = findPlayerById(playId);
        p.eventHandler(eventValue, cardNum);

        // 根据不同的事件跳转进不同的状态
        // 本地的状态切换
        // 首先必须先获取 machine
        StateMachine machine = CacheUtil.getStateMachine();

        if(eventValue == PONG.getValue()) {
            // data: DsMessage<GameInfoRequest>
            machine.setCurrentState(STATE_TAKE_OUT_CARD_ID, null);
        }else {
            DsResult data = DsResult.data(new GameInfoResponse().setUserId(playId));

            if(eventValue == DIRECT_GANG.getValue()
                    || eventValue == SELF_GANG.getValue()
                    || eventValue == IN_DIRECT_GANG.getValue()) {

                // data : DsResult<GameInfoResponse>
                machine.setCurrentState(STATE_TAKE_CARD_ID, data);
            }else if(eventValue == IN_DIRECT_HU.getValue()
                    || eventValue == SELF_HU.getValue()) {
                System.out.println("胡牌");
            }else if(eventValue == NOTHING.getValue()) {
                // 同样进行判断下一回合是否为自己
                if(isCurAround(playId)) {
                    machine.setCurrentState(STATE_TAKE_CARD_ID, data);
                }else {
                    machine.setCurrentState(STATE_WAIT_ID, data);
                }
                System.out.println("过牌");
            }else {
                throw new IllegalArgumentException("没有设置的事件");
            }
        }
    }
}
