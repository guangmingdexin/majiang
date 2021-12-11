package ds.guang.majing.client.action;

import ds.guang.majing.client.event.Event;
import ds.guang.majing.common.DsResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guangyong.deng
 * @date 2021-12-10 15:31
 */
public class ActionFactory {

    private static ConcurrentHashMap<Class<? extends Event>, Action> factory = new ConcurrentHashMap<>();

    static {

    }

    public static void register(Class<? extends Event> event, Action action) {

        if(factory.containsKey(event)) {
            return;
        }
        factory.put(event, action);

    }

    public static Action get(Event event) {
        Action action = factory.get(event);

        if(action == null) {
            // TODO 返回一个默认业务实现类
            return (Action<Event, DsResult>) event1 -> DsResult.empty();
        }

        return action;
    }
}
