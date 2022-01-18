package ds.guang.majing.common.game.card;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * @author asus
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MaJiangEvent.class, name = "event")
})
public interface GameEvent extends Serializable {


    /**
     *
     * 设置玩家游戏事件
     *
     * @param event 事件
     */
    void setEvent(int event);


    /**
     *
     * 判断是否有特殊事件
     *
     * @return
     */
    boolean isEvent();


    /**
     *
     * 获取事件类型
     *
     * @return 事件类型
     */
    int getEvent();
}
