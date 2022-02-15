package ds.guang.majing.common.game.machines;

import ds.guang.majing.common.game.rule.Rule;
import ds.guang.majing.common.state.StateMachine;

/**
 * @author guangyong.deng
 * @date 2021-12-23 16:30
 */
public class DefaultMachineFactory implements StateMachineFactory {

    public static final StateMachineFactory FACTORY = new DefaultMachineFactory();

    private DefaultMachineFactory() {}

    @Override
    public StateMachine create(Rule rule) {
        // 但是这样创造的 StateMachine 仍然是共用的
        // 初始化规则
        // Rule<String, StateMachine<String, String, DsResult>> rule = new V3PlatFormRule();
        rule.create("V3-RULE");
        Object ruleActor = rule.getRuleActor();
        return (StateMachine) ruleActor;
    }
}