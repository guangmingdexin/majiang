package ds.guang.majing.client.rule.platform;

import ds.guang.majing.client.event.LoginRequest;
import ds.guang.majing.client.event.PrepareRequest;
import ds.guang.majing.client.event.Request;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.dto.User;
import ds.guang.majing.common.rule.AbstractRule;
import ds.guang.majing.common.rule.Rule;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.state.StateMachine;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static ds.guang.majing.common.DsConstant.*;

/**
 * 规定平台规则
 * @author asus
 */
public class PlatFormRuleImpl extends AbstractRule<String, StateMachine<String, String, DsResult>> {

    private Supplier<State<String, String, DsResult>> loginStateSupplier = () -> new AbstractStateImpl<String, String, DsResult>(STATE_LOGIN_ID) {

        @Override
        public State<String, String, DsResult> onEvent(String eventId, String nextStateId) {
            return onEvent(eventId, nextStateId, data -> {
                CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                // 构造一个 LoginRequest 对象
                                Request request = new LoginRequest((User) data);
                                return request.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return DsResult.error();
                        }).thenApply(dsResult -> {
                            if(dsResult.isOk()) {
                             // 进入游戏
                            System.out.println("thread-name: " + Thread.currentThread().getName() + " success 进入游戏!");
                            }else {
                                // 跳出弹框
                            }
                            return dsResult;
                        }).exceptionally(ex -> {
                            System.out.println("发生异常"+ex.getMessage());
                            return DsResult.error();
                        });

                return DsResult.data(this);
            });
        }
    };

    private Supplier<State<String, String, DsResult>> platformSupplier = () -> new AbstractStateImpl<String, String, DsResult>(STATE_PLATFORM_ID) {};

    @Override
    public Rule<String, StateMachine<String, String, DsResult>> create(String s) {

        State<String, String, DsResult> loginState = loginStateSupplier.get();
        loginState.onEvent(EVENT_LOGIN_ID, STATE_PLATFORM_ID);


        State<String, String, DsResult> platformState = platformSupplier.get();
        platformState.onEvent(EVENT_PLATFORM_ID, STATE_PREPARE_ID);
        platformState.onEvent(EVENT_MATCH_FRIEND_ID, STATE_MATCH_FRIEND_ID);

        platformState.onEntry(data -> {
           // 1.获取客户端资源
            System.out.println("加载客户端资源！加载界面");
            return null;
        });
        platformState.onEvent(EVENT_PREPARE_ID, STATE_PREPARE_ID, data -> {
            // 1.向服务器发送加入游戏消息，并传入自身的游戏 id
            CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            // 构造一个 LoginRequest 对象
                            Request request = new PrepareRequest((GameUser) data);
                            return request.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return DsResult.error();
                    }).thenApply(dsResult -> {
                if(dsResult.isOk()) {
                    // 进入游戏
                    System.out.println("thread-name: " + Thread.currentThread().getName() + " 排队准备中!");
                }else {
                    // 跳出弹框
                }
                return dsResult;
            }).exceptionally(ex -> {
                System.out.println("发生异常"+ex.getMessage());
                return DsResult.error();
            });

            return DsResult.data(this);
        });

        StateMachine<String, String, DsResult> ruleActor = getRuleActor();
        ruleActor.registerInitialState(loginState);
        ruleActor.registerState(platformState);


        ruleActor.start();

        return this;
    }

}
