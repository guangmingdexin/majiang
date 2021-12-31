package ds.guang.majiang.server.machines;

import ds.guang.majiang.server.rule.V3PlatFormRule;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.rule.Rule;
import ds.guang.majing.common.state.StateMachine;

/**
 * @author guangyong.deng
 * @date 2021-12-23 16:30
 */
public class DefaultMachineFactory implements StateMachineFactory {

    public static final StateMachineFactory FACTORY = new DefaultMachineFactory();

    private DefaultMachineFactory() {}

    @Override
    public StateMachine create() {
        // 但是这样创造的 StateMachine 仍然是共用的
        // 初始化规则
        Rule<String, StateMachine<String, String, DsResult>> rule = new V3PlatFormRule();
        rule.create("V3-RULE");
        Object ruleActor = rule.getRuleActor();
        return (StateMachine) ruleActor;
    }
}
