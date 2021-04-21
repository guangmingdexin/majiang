package com.guang.majiangclient.client.handle.action;

import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.util.ClassUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ActionFactory
 * @Description 客户端业务处理工厂，通过将请求数据的事件类型与业务处理类进行绑定
 * @Author guangmingdexin
 * @Date 2021/4/16 15:04
 * @Version 1.0
 **/
public class ActionFactory  {

    private static ConcurrentHashMap<Event, ClientAction> factory =
            new ConcurrentHashMap<>();


    public static void registerAll(Set<Class<?>> set) {
        for (Class<?> clazz : set) {
            ClientAction clientAction;
            try {
                 clientAction = (ClientAction) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            Action action = clazz.getAnnotation(Action.class);
            if(action == null) {
                throw new NullPointerException("非法的 action");
            }
            Event event = action.event();
            if(factory.containsKey(event)) {
                return;
            }
            factory.put(event, clientAction);
        }
    }

    public static ClientAction action(Event event) {
        ClientAction action = factory.get(event);

        if(action == null) {
            // TODO 返回一个默认业务实现类
            return new DefaultAction();
        }

        return action;
    }
}
