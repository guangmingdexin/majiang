package ds.guang.majing.common.game.card;

import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.state.StateMachine;
import ds.guang.majing.common.util.ResponseUtil;
import lombok.Getter;

import java.util.PriorityQueue;

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

        if(e.contain(MaJiangEvent.PONG)
                || e.contain(MaJiangEvent.DIRECT_GANG)) {

            // 第一种情况
            // 异常判断
            // 同一回合，最多一个玩家可以pong
            if(!priorityQueue.isEmpty()) {
                throw new IllegalArgumentException("错误的游戏事件");
            }

            eventStatus = cancel ? EVENT_STATUS_CANCEL : eventStatus;

        }else if (!e.contain(MaJiangEvent.IN_DIRECT_HU)) {
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
        Room.write(id, ResponseUtil.response(action));

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
    public int nextRound(int eventValue, String id, Room room) {

        int direction = room.findPlayerById(id).getDirection();

        if(eventValue == MaJiangEvent.NOTHING.getValue()) {
            room.setCurRoundIndex(direction + 1);
            // 将下家的状态由等待切换到 摸牌状态

            StateMachine stateMachine = room.nextPlayerState();
            stateMachine.setCurrentState(STATE_TAKE_CARD_ID, id);
        }

        // 1.首先判断是否还有事件没有处理完成
        if(!priorityQueue.isEmpty()) {

            // 当前房间回合切换到下家
            if(eventValue == MaJiangEvent.PONG.getValue()
                    || eventValue == MaJiangEvent.IN_DIRECT_HU.getValue()) {

                room.setCurRoundIndex(direction + 1);
                // 将下家的状态由等待切换到 摸牌状态
                StateMachine stateMachine = room.nextPlayerState();
                stateMachine.setCurrentState(STATE_TAKE_CARD_ID, null);

            }else if(eventValue == MaJiangEvent.DIRECT_GANG.getValue()) {
                // 当前回合切换到事件发起者
                room.setCurRoundIndex(direction);
            }
        }
        return room.getCurRoundIndex();
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
