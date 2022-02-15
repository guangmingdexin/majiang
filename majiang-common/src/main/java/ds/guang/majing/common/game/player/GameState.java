package ds.guang.majing.common.game.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author guangyong.deng
 * @date 2022-02-14 14:05
 */
@Getter
@AllArgsConstructor
public enum GameState {

    /**
     * 一局游戏不同阶段
     */
    Game_Start(1, "游戏开始"),
    Game_Process(2, "游戏进行"),
    Game_Over(3, "游戏结束");

    int state;

    String desc;



}
