package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.util.ClassUtil;
import ds.guang.majing.common.state.State;

import java.util.*;

/**
 * @author guangyong.deng
 * @date 2021-12-23 15:19
 */
public class ActionManager {

    private static final HashMap<String, List<Action>> map = new HashMap<>();

    static {
        // 直接扫描包先对应进去
        // package 应该能够动态配置

        Set<Class<?>> clazz = ClassUtil.getClassFromPath(
                "ds.guang.majiang.server.layer",
                StateMatchAction.class, true);


        clazz.stream()
                .filter(aClass -> Action.class.isAssignableFrom(aClass))
                .filter(aClass -> ClassUtil.exits(aClass, StateMatchAction.class))
                .forEach(aClass -> {
                    // 获取注解上的值
                    StateMatchAction stateMatchAction = aClass.getAnnotation(StateMatchAction.class);
                    String stateId = stateMatchAction.value();
                    // 判断不能为空
                    Objects.requireNonNull(stateId, "stateId cannot be empty!");
                    List<Action> actions = map.getOrDefault(stateId, new ArrayList<>());
                    // 通过反射构造一个实例
                    try {
                        Action action = (Action) aClass.newInstance();
                        actions.add(action);
                        map.put(stateId, actions);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

    }

    public static void onEvent(State... states) {

       // 怎样自动找到 state 对应的 contain，不用每次都需要去创建
        // 可以通过注解，根据 stateId 来进行设置（是否有这个必要）
        // 默认取最大优先级
        for (State state : states) {
            Action maxOrderAction = getMaxOrderAction(state.getId().toString());
            maxOrderAction.handler(state);
        }
    }


    public static Action getMaxOrderAction(String stateId) {

        // 对于有多个默认实现的 Action 的 State 可以进行优先级选择
        // 来绑定不同的事件
        List<Action> actions = map.get(stateId);

        if(actions == null || actions.isEmpty()) {
            throw new NullPointerException("actions is empty!");
        }

        Action maxOrderAction = actions.get(0);

        for (Action action : actions) {

            int order = action.getOrder();
            if(order > maxOrderAction.getOrder()) {
                maxOrderAction = action;
            }
        }
        return maxOrderAction;
    }

    public static void print() {
        System.out.println("打印 ActionManager !");
        map.forEach((key, value) -> {
            System.out.println(key + " " +  value);
        });
    }
}
