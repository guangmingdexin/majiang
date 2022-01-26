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
        @JsonSubTypes.Type(value = MaGameEvent.class, name = "event")
})
public interface GameEvent extends Serializable, Comparable {


    /**
     *
     * 获取事件 value
     *
     * @return 事件类型
     */
    int getEvent();


    /**
     *
     * 获取优先级
     *
     * @return 优先级
     */
    int getPriority();


    /**
     *
     * 获取事件对象
     *
     * @return 事件对象
     */
    String getEventName();


    /**
     *
     * 获取事件发起者的 id
     *
     * @return id
     */
    String getPlayId();


    /**
     *
     * 是否为空事件
     *
     * @return false 非空， true 空
     */
    boolean isEmpty();


    /**
     *
     * 服务判断能不能执行某个游戏事件
     *
     * @param o 客户端发送的游戏事件
     * @return
     */
    boolean contain(GameEvent o);

}
