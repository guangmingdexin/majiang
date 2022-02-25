package ds.guang.majiang.server.network;

import lombok.ToString;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guangyong.deng
 * @date 2022-02-25 17:18
 */
@ToString
public class Context<K, V> {

    private final  ConcurrentHashMap<K, Entry> map = new ConcurrentHashMap<>();

    public void put(K key, V v) {

        map.put(key, new Entry(v));
    }


    public V get(K key) {
        return map.get(key).get();
    }


    class Entry extends WeakReference<V> {

        public Entry(V referent) {
            super(referent);
        }

    }
}
