package ds.guang.majing.common.game.card;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 *
 * 棋牌类对象顶级接口
 *
 * @author guangyong.deng
 * @date 2021-12-22 14:09
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MaJiang.class, name = "card")
})
public interface Card extends Serializable {

    /**
     *
     * 获取 棋牌的 value 值
     *
     * @return value 值
     */
    int value();

}
