package ds.guang.majing.common.game.machines;

import ds.guang.majing.common.game.rule.Rule;
import ds.guang.majing.common.state.StateMachine;

/**
 * @author guangyong.deng
 * @date 2021-12-23 14:30
 */
public interface StateMachineFactory {


    /**
     *
     * 通过规则制定一个状态机来约束状态机的行为
     *
     * @return 状态机
     */
    StateMachine create(Rule rule);
}
