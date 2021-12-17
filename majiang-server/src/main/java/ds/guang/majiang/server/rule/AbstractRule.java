package ds.guang.majiang.server.rule;

import ds.guang.majiang.server.player.Player;
import ds.guang.majing.common.state.StateMachine;

/**
 *
 * 不同的游戏会有不同的规则
 * 但是不同的游戏也会有相同的共性元素，作为麻将棋牌游戏
 * 必定包含以下元素：玩家，动作，状态
 * 返回一个设定好的状态机
 *
 * @author guangyong.deng
 * @date 2021-12-17 13:58
 */
public abstract class AbstractRule<T, R> implements Rule<T, R> {

    protected Player player;

    protected R stateMachine;

    @Override
    public R getRuleActor() {
        return stateMachine;
    }

    public Rule setStateMachine(R stateMachine) {
        this.stateMachine = stateMachine;
        return this;
    }
}
