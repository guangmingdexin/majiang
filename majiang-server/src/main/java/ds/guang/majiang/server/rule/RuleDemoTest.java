package ds.guang.majiang.server.rule;

import ds.guang.majing.common.event.Event;
import ds.guang.majing.common.state.StateMachine;

/**
 * @author guangyong.deng
 * @date 2021-12-17 15:42
 */
public class RuleDemoTest {

    public static void main(String[] args) {

        Rule<String, StateMachine<String, String, Object>> rule = new PlayerChuanMaRule();

        rule.create("川麻");

        StateMachine<String, String, Object> ruleActor = rule.getRuleActor();

        ruleActor.start();

        ruleActor.event(new Event<>("test-event"));

    }
}
