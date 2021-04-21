package imageloader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName MapTest
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/9 16:27
 * @Version 1.0
 **/
public class MapTest {

    public static void main(String[] args) {
        LRU<Integer, Integer> lru = new LRU<>(5, 0.75f);
        lru.put(1, 1);
        lru.put(2, 2);
        lru.put(3, 3);
        lru.put(4, 4);
        lru.put(5, 5);
        lru.put(6, 6);
        lru.put(7, 7);

        System.out.println(lru.get(1));
        System.out.println(lru.get(4));

         lru.put(6, 666);
        System.out.println(lru);
    }
}

class LRU<K, V> extends LinkedHashMap<K, V> {

    private int capacity;

    public LRU(int capacity, float loadFactor) {
        super(capacity, loadFactor, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > this.capacity;
    }
}
