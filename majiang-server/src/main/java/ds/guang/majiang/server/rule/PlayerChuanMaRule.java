package ds.guang.majiang.server.rule;

import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;

import java.util.function.Supplier;

/**
 *
 *
 * @author guangyong.deng
 * @date 2021-12-17 14:03
 */
public class PlayerChuanMaRule extends AbstractRule<String, StateMachine<String, String, Object>> {

    Supplier<State<String, String, DsResult>> prepareStateSupplier = () -> new AbstractStateImpl<String, String, DsResult>(DsConstant.STATE_PREPARE_ID) {
        @Override
        public void entry(Object data) {
            // 1.服务端-获取玩家传入的 channelId, userId, username, 头像
            // 2.创建一个房间
            super.entry(data);
        }
    };

    Supplier<State<String, String, Object>> initialStateSupplier = () -> new AbstractStateImpl<String, String, Object>(DsConstant.STATE_INITIAL_ID) {

        @Override
        public void entry(Object data) {
            System.out.println("开始加载配置！");
        }

        @Override
        public State<String, String, Object> onEvent(String eventId, String nextState) {

            return onEvent(eventId, nextState, handle -> {
                System.out.println("进入下一个状态");
                //
                return this;
            });
        }
    };

    @Override
    public Rule create(String t) {
        // 对不同游戏场景下设置不同的规则
        // 游戏固定步骤
        // 1.初始化游戏 -> 2. 加载图片 -> 3. 玩家操作 -> 4. 每个动作规定不同的结果
        // 初步目标实现通过 加载不同的实现状态，初始化时不同情况
        stateMachine = new StateMachine<>();
        State<String, String, Object> initState = initialStateSupplier.get();
        // 还需要为每个状态注册相关的 事件
        initState.onEvent("test-event", "test-state");
        stateMachine.registerInitialState(initState);
        stateMachine.registerState(new AbstractStateImpl<String, String, Object>("test-state") {

            @Override
            public State<String, String, Object> onEvent(String eventId, String nextState) {

                return onEvent(eventId, nextState, handler -> {
                    //
                    System.out.println("摸牌！");
                    return this;
                });
            }

            @Override
            public void entry(Object data) {
                System.out.println("进入下一个状态了！");
            }
        });
        return this;
    }
}
