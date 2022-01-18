package ds.guang.majing.common.game.rule;


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

    protected String ruleId;

    protected R stateMachine;

    @Override
    @SuppressWarnings("unchecked")
    public R getRuleActor() {
        if(stateMachine == null) {
            R r = (R) new StateMachine<>();
            this.stateMachine = r;
            return r;
        }
        return stateMachine;
    }

    public Rule<T, R> setStateMachine(R stateMachine) {
        this.stateMachine = stateMachine;
        return this;
    }
}
