package ds.guang.majing.common.game.machines;

import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.exception.DsBasicException;
import ds.guang.majing.common.state.StateMachine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guangyong.deng
 * @date 2021-12-23 13:40
 */
public class StateMachines  {

    private static final Map<String, StateMachine<String, String, DsResult>> map  = new ConcurrentHashMap<>(16);

    private static final AtomicInteger DEFAULT_MAX_LIMIT_CAPACITY = new AtomicInteger(65536);


    /**
     *
     * 根据用户标识获取对应的状态机
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
   public static StateMachine<String, String, DsResult> get(String key) {

        if(map.containsKey(key)) {
            return map.get(key);
        }

       // StateMachine defaultValue = DefaultMachineFactory.FACTORY.create();

      //  put(key, defaultValue);

        return null;
    }


    /**
     *
     * 存入容器
     *
     * @param key
     * @param item
     * @return
     */
    public static boolean put(String key, StateMachine<String, String, DsResult> item) {
        // 首先判断是否超过最大容量
        if(map.size() >= DEFAULT_MAX_LIMIT_CAPACITY.get()) {
            // 这里应该抛出一个异常，表示无法再处理了
            throw new DsBasicException("the map size is max capacity!");
        }
        map.put(key, item);
        return true;
    }


    /**
     *
     * 移除
     *
     * @param key
     * @return
     */
    public static boolean remove(String key) {
        map.remove(key);
        return true;
    }

    /**
     *
     * 预留的增加最大容量的方法（或许应该设置的更为智能一点，实时的通过堆内存来判断）
     *
     *
     * @param capacity
     */
    public static void setMaxCapacity(int capacity) {

        if(capacity <= 0) {
            throw new DsBasicException("capacity must be bigger than zero");
        }

        DEFAULT_MAX_LIMIT_CAPACITY.getAndSet(capacity);
    }

}
