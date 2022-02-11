package ds.guang.majing.common.game.card;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ds.guang.majing.common.game.room.Room;

/**
 * @author guangyong.deng
 * @date 2022-01-26 9:11
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MaEventHandler.class, name = "eventHandler")
})
public interface GameEventHandler {


    /**
     *
     * 处理游戏事件，主要包括状态切换，回合转换
     * 必须保证是线程安全的
     *
     * @return boolean 是否为空
     */
    boolean isEmpty();



    /**
     *
     * 添加事件进入 handler
     *
     * @param event 游戏事件
     */
    void addEvent(GameEvent event);


    /**
     * 通知客户端可以执行事件
     *
     * 1.游戏事件必须按照队列顺序来执行
     * 2.一个回合之类，在前一个游戏事件未得到回复之前或者未超时之前，不能执行下一个游戏事件
     *
     * @param userId 请求事件的用户 id
     * @param cancel  是否取消低优先级任务
     *
     */
    void announce(String userId, boolean cancel);


    /**
     * 唤醒下一个
     */
    void announceNext();

    /**
     *
     * 进入下一回合
     * @param eventValue 游戏事件的的值
     * @param id 玩家 id
     * @param room 房间
     *
     * @return 回合索引
     */
    int nextRound(int eventValue, String id, Room room);

}
