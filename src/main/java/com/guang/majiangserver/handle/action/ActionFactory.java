package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.util.ClassUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName FactoryAction
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/8 14:51
 * @Version 1.0
 **/
public final class ActionFactory {

    @Getter
    @Setter
    public static class ActionBean {
        Class<?> request;
        Class<?> response;
        ServerAction action;

        ActionBean(Class<?> request, Class<?> response, ServerAction action) {
            this.request = request;
            this.response = response;
            this.action = action;
        }
    }

    private static ConcurrentHashMap<Class<?>, ActionBean> factory = new ConcurrentHashMap<>();

    public static void register(ServerAction action) {
        // 获取 ServerAction 的泛型实际类型
        Class<?> request = ClassUtil.findInterfaceParameter(action, ServerAction.class, 0, 0);

        Class<?> response = ClassUtil.findInterfaceParameter(action, ServerAction.class, 0, 1);
        // 构造 ActionBean 的实例对象
        ActionBean actionBean = new ActionBean(request, response, action);
        // 加入到容器中
        factory.put(request, actionBean);
    }

    public static void registerAll(Set<Class<?>> classes) {

        for (Class<?> clazz : classes) {
            ServerAction action;
            try {
                // TODO 如果 action 有参数，需要重新设计
                action = (ServerAction) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            // 获取 ServerAction 的泛型实际类型
            Class<?> request = ClassUtil.findInterfaceParameter(action, ServerAction.class, 0, 0);
            // 判断是否存在
            //  批次注册不允许二次注册
            if(factory.containsKey(request)) {
                return;
            }
            Class<?> response = ClassUtil.findInterfaceParameter(action, ServerAction.class, 0, 1);
            // 构造 ActionBean 的实例对象
            ActionBean actionBean = new ActionBean(request, response, action);
            // 加入到容器中
            factory.put(request, actionBean);
        }
    }

    public static ActionBean action(Class<?> request) {
        ActionBean actionBean = factory.get(request);
        if(actionBean == null) {
            throw new NullPointerException("actionBean 还未注册！");
        }
        return actionBean;
    }

}
