package ds.guang.majing.client.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author guangyong.deng
 * @date 2022-02-22 13:50
 */
@Getter
@AllArgsConstructor
public enum  CardState {


    /**
     * 游戏棋牌状态
     */
    CARD_INIT("init", "初始化"),
    CARD_FOCUS("focus", "选中"),
    CARD_OUT("out", "出牌");

    private String value;

    private String desc;

}
