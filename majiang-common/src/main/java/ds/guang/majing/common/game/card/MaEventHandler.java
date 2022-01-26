package ds.guang.majing.common.game.card;

import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.util.ResponseUtil;

import java.util.PriorityQueue;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 * @author guangyong.deng
 * @date 2022-01-26 9:21
 */
public class MaEventHandler implements GameEventHandler {

    /**
     * 此次游戏事件个数
     */
    private int eventNum;


    private static int maxEventNum = 5;


    private int huCount = 0;

    /**
     * 游戏事件执行队列
     */
    private PriorityQueue<GameEvent> priorityQueue;

    public MaEventHandler() {
        // 一次最多四个事件加入
        eventNum = 0;
        priorityQueue = new PriorityQueue<>(maxEventNum, Comparable::compareTo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handler(GameEvent event) {
        // 1.同一回合中，需要收集其他玩家的游戏事件
        // 2.首先比较是否为高优先级事件
        if(priorityQueue == null || priorityQueue.isEmpty()) {
            throw new NullPointerException("游戏事件状态错误");
        }

        GameEvent serverGameEvent = priorityQueue.peek();
        // 通知相关客户端
        String id = serverGameEvent.getPlayId();

        GameInfoResponse infoResponse = new GameInfoResponse();

        DsMessage<DsResult<GameInfoResponse>> eventResp = DsMessage.build(
               EVENT_IS_GAME_EVENT_ID,
                id,
                infoResponse);

        // 判断是否可以执行事件
        if(serverGameEvent.contain(event)) {

            // 客户端可以执行游戏事件了
            infoResponse.setEventStatus(EVENT_STATUS_ACTION);

            priorityQueue.poll();
            // 胡牌的优先级
            int huPriority = MaJiangEvent.IN_DIRECT_HU.getPriority();
            if(event.getPriority() == huPriority) {
                // 判断一下后面的任务优先级，如果最大任务优先级低于胡牌的，直接全部放弃
                // 否则继续
                huCount ++;

                if(!priorityQueue.isEmpty() && priorityQueue.peek().getPriority() < huPriority) {
                    // 直接将后面的游戏事件无效化
                    // 场景：玩家 A 胡牌，玩家 B 的 碰牌/杠牌事件无效
                    while (!priorityQueue.isEmpty()) {

                        GameEvent o = priorityQueue.poll();

                        DsMessage<DsResult<GameInfoResponse>> cancel = DsMessage.build(
                                EVENT_IS_GAME_EVENT_ID,
                                o.getPlayId(),
                                new GameInfoResponse()
                                        .setUserId(o.getPlayId())
                                        .setEventStatus(EVENT_STATUS_CANCEL)
                        );

                        // 发信息
                        Room.write(o.getPlayId(), ResponseUtil.response(cancel));

                    }
                }
            }

        }else {
            // 继续等待
            infoResponse.setEventStatus(EVENT_STATUS_WAIT);
        }

        // 发送消息
        Room.write(id, ResponseUtil.response(eventResp));

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
    public int nextRound() {
        // 1.首先判断是否还有事件没有处理完成

        return 0;
    }
}
