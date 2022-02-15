package ds.guang.majing.common.game.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.game.room.ServerFourRoom;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Objects;

/**
 * @author guangyong.deng
 * @date 2022-01-26 9:43
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MaGameEvent implements GameEvent {

    /**
     * 实际执行的游戏事件类型
     */
    private MaJiangEvent actionEvent;

    /**
     * 游戏 事件发起人 id
     */
    private String playId;

    /**
     * 客户端对应的事件名称
     */
    private String eventName;


    private Map<MaJiangEvent, Integer> selectEvent;

    @JsonIgnore
    @Override
    public int getEvent() {
        return actionEvent == null ? -1: actionEvent.getValue();
    }

    @JsonIgnore
    @Override
    public int getPriority() {

        if(selectEvent == null) {
            throw new NullPointerException("没有可触发的事件");
        }

        int maxPriority = -1;

        for (MaJiangEvent e : selectEvent.keySet()) {
            maxPriority = Math.max(e.getPriority(), maxPriority);
        }

        return maxPriority;
    }

    @Override
    public String getEventName() {
        return eventName;
    }


    @Override
    public String getPlayId() {
        return playId;
    }

    @JsonIgnore
    @Override
    public boolean isEmpty() {
        return selectEvent == null || selectEvent.isEmpty();
    }


    /**
     *
     * 自定义的比较器
     *
     * @param o 比较对象
     * @return -1 优先级小， 0 优先级相等， 1 优先级大
     */
    @Override
    public int compareTo(Object o) {

        if(o == null) {
            return 1;
        }

        if(o instanceof GameEvent) {

            GameEvent e = (GameEvent) o;

            // 1.事件优先级
            // 2.发起事件玩家的位置，即按照当前回合顺时针方向进行

            if(this.getPriority() == e.getPriority()) {

                ServerFourRoom room = ServerFourRoom.getRoomById(playId);
                int roundIndex = room.getCurRoundIndex();
                // 获取各个玩家的位置
                int cur = room.direction(playId);
                int compare = room.direction(e.getPlayId());

                // 两种情况
                // 一：都比 roundIndex 大，越小的顺序越先执行
                // 二：其余情况，越靠近 roundIndex，即差值越大的越先执行，越大的越先执行

                if(cur >= roundIndex && compare >= roundIndex) {

                    return cur <= compare ? 1 : -1;
                }
                return (cur - roundIndex) >= (compare - roundIndex) ? 1: -1;

            }

            // 因为游戏事件的特殊性，其不太可能出现同一级别的事件
            // 只有一种例外，同时胡牌（放炮），所以只需要单独考虑就行了
            return this.getPriority() - e.getPriority();
        }

        throw new ClassCastException("o is not GameEvent");

    }


    @Override
    public boolean contain(Object o) {

        if(!(o instanceof MaJiangEvent)) {
            throw new IllegalArgumentException("不是麻将游戏事件");
        }

        return selectEvent != null
                && selectEvent.containsKey(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaGameEvent that = (MaGameEvent) o;
        return actionEvent == that.actionEvent &&
                Objects.equals(playId, that.playId) &&
                Objects.equals(eventName, that.eventName) &&
                // Map 相等是如何求的
                Objects.equals(selectEvent, that.selectEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionEvent, playId, eventName, selectEvent);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"actionEvent\":").append(actionEvent)
                .append(", \"playId\":").append(playId)
                .append(", \"eventName\":").append(eventName)
                .append(", \"selectEvent\":").append(selectEvent)
                .append('}');
        return sb.toString();
    }
}
