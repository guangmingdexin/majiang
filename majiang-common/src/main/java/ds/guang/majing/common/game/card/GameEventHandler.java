package ds.guang.majing.common.game.card;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
     *
     * @param event 游戏事件
     */
    void handler(GameEvent event);



    /**
     *
     * 添加事件进入 handler
     *
     * @param event 游戏事件
     */
    void addEvent(GameEvent event);




    /**
     *
     * 进入下一回合
     *
     * @return 回合索引
     */
    int nextRound();

}
