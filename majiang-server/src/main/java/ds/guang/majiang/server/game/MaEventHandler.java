package ds.guang.majiang.server.game;

import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.GameEventHandler;
import ds.guang.majing.common.game.card.MaGameEvent;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.player.GameState;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.ServerFourRoom;
import ds.guang.majing.common.state.StateMachine;

import lombok.Getter;

import java.util.PriorityQueue;

import static ds.guang.majing.common.game.card.MaJiangEvent.*;
import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2022-01-26 9:21
 */
@SuppressWarnings("unchecked")
public class MaEventHandler implements GameEventHandler {

    /**
     * 此次游戏事件个数
     */
    private int eventNum;


    private static int maxEventNum = 5;


    /**
     * 游戏事件执行队列
     */
    @Getter
    private  PriorityQueue<GameEvent> priorityQueue;

    public MaEventHandler() {
        // 一次最多四个事件加入
        eventNum = 0;
        priorityQueue = new PriorityQueue<>(maxEventNum, Comparable::compareTo);
    }


    @Override
    public boolean isEmpty() {
        return priorityQueue == null || priorityQueue.isEmpty();
    }

    @Override
    public void addEvent(GameEvent event) {

        if(event == null) {
            return;
        }

        // 默认是 小顶堆
        priorityQueue.offer(event);

        eventNum ++;

        if(eventNum >= maxEventNum) {
            throw new IllegalArgumentException("超过最大事件处理数量");
        }
    }

    @Override
    public void announce(String userId, boolean cancel) {

        System.out.println("通知事件客户端！");

        if(priorityQueue.isEmpty()) {
            return;
        }

        GameEvent e = priorityQueue.poll();

        System.out.println("e: " + e);

        if(!userId.equals(e.getPlayId())) {
            // 此时还不是该玩家，挂起请求，待前一个玩家将事件处理完成之后，进行唤醒
            return;
        }

        // 只有四种情况
        // 1.玩家有pong/gang ，其余玩家无事件
        // 2.玩家有pong/gang, 其余玩家有一个或多个可以hu
        // 3.玩家有pong/gang/hu, 其余玩家无事件
        // 4.玩家有 hu，其他玩家无事件
        // 4.都无事件
        String eventStatus = EVENT_STATUS_ACTION;

        if(e.contain(PONG)
                || e.contain(DIRECT_GANG)) {

            // 第一种情况
            // 异常判断
            // 同一回合，最多一个玩家可以pong
            if(!priorityQueue.isEmpty()) {
                throw new IllegalArgumentException("错误的游戏事件");
            }

            eventStatus = cancel ? EVENT_STATUS_CANCEL : eventStatus;

        }else if (!e.contain(IN_DIRECT_HU)) {
            throw new IllegalArgumentException("不是该阶段处理的游戏事件");
        }

        // 通知相关客户端
        String id = e.getPlayId();
        GameInfoResponse infoResponse = new GameInfoResponse()
                .setEventStatus(eventStatus)
                .setUserId(id)
                .setEvent(e)
                .setCurRoundIndex(-1);

        DsMessage<DsResult<GameInfoResponse>> action = DsMessage.build(
                EVENT_IS_GAME_EVENT_ID,
                id,
                DsResult.data(infoResponse));

        // 发信息
        ServerFourRoom.write(id, ResponseUtil.response(action));

    }

    @Override
    public void announceNext() {
        // 1.调用者只能为hu 事件

        if(priorityQueue.isEmpty()) {
            // 所有事件都已经结束了
            return;
        }
        // 类似于唤醒头部节点
        announce(priorityQueue.peek().getPlayId(), true);

    }


    @Override
    public int nextRound(GameEvent event, Room r) {

        ServerFourRoom room = (ServerFourRoom) r;

        String id = event.getPlayId();
        int eventValue = event.getEvent();
        // 获取玩家
        Player p = room.findPlayerById(id);
        // 获取方位
        int direction = p.getDirection();

        // 1.首先判断是否还有事件没有处理完成
        if(priorityQueue.isEmpty()) {

            if(eventValue == PONG.getValue()) {
                // 当前回合切换为事件发起者
                room.setCurRoundIndex(room.skipNextRoundIndex(direction));
                
                // 将当前玩家的状态切换为出牌状态（此时应该不能暗杠，也不能自摸）
                StateMachine stateMachine = room.nextPlayerState();
                stateMachine.setCurrentState(STATE_TAKE_OUT_CARD_ID, id);

            }else if(eventValue == DIRECT_GANG.getValue()
            || eventValue == SELF_GANG.getValue()
            || eventValue == IN_DIRECT_GANG.getValue()) {
                // 当前回合切换为事件发起者
                room.setCurRoundIndex(room.skipNextRoundIndex(direction));
                // 将当前玩家的状态切换为出牌状态
                StateMachine stateMachine = room.nextPlayerState();
                stateMachine.setCurrentState(STATE_TAKE_CARD_ID, id);
            } else if(event.getEvent() == IN_DIRECT_HU.getValue()
                    || event.getEvent() == SELF_HU.getValue()) {
                room.setCurRoundIndex(room.skipNextRoundIndex(direction + 1));
                // 当前玩家状态切换为本局游戏结束状态
                StateMachine curState = room.findPlayerState(id);
                curState.setCurrentState(STATE_GAME_OVER_ID, id);
                p.setGameState(GameState.Game_Over);
                // TODO: 1.计算分数， 2.计算场上还有多少玩家没有 胡牌，判断是否能够结束游戏
                // TODO: 更改回合方法需要进行更改，需要跳过游戏状态为 over 的玩家
                int countHu = room.getCountHu() + 1;
                room.setCountHu(countHu);

            } else if(eventValue == NOTHING.getValue()) {
                // 如果点击过，则正常到下一位玩家回合，而不是事件发起者的下一回合
                // 1.获取前一个回合
                // 2.前一个回合加一
                room.setCurRoundIndex(room.skipNextRoundIndex(room.getPrevRoundIndex() + 1));
                // 将下家的状态由等待切换到 摸牌状态
                StateMachine stateMachine = room.nextPlayerState();

                if(room.isCurAround(room.nextPlayerId())) {
                    stateMachine.setCurrentState(STATE_TAKE_CARD_ID, id);
                }else {
                    stateMachine.setCurrentState(STATE_WAIT_ID, id);
                }
            }
        }else {
            // 说明现在还有事件没有处理完，无法正常进行回合
            room.setCurRoundIndex(-1);
        }
        return room.getCurRoundIndex();
    }

    @Override
    public void over(Room room) {

        // 1.向还未胡牌的玩家发送信息
        // 2.如果是由胡牌引起的话

        GameInfoResponse info = new GameInfoResponse()
                .setCard(null)
                .setEvent(new MaGameEvent(MaJiangEvent.OVER, null, "Over", null));

        // 通知其他玩家全部进入 over 状态
        Player[] players = room.getPlayers();

        for (Player player : players) {

            if(!player.isHu()) {

                String userId = player.id();
                ((ServerFourRoom)room).findPlayerState(userId).setCurrentState(STATE_GAME_OVER_ID, null);
                ServerFourRoom.write(userId, DsMessage.build(
                        EVENT_OVER_ID,
                        userId,
                        DsResult.data(info)
                ));
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"eventNum\":").append(eventNum)
                .append(", \"priorityQueue\":").append(priorityQueue)
                .append('}');
        return sb.toString();
    }
}
