package gameTest;

import java.util.Map;

/**
 * @ClassName LinkedHashMap
 * @Author guangmingdexin
 * @Date 2021/5/18 20:27
 * @Version 1.0
 **/
public class LRUCache {

    public LRU lru = null;

    public LRUCache(int capacity) {
        lru = new LRU(capacity, 0.75f);
    }

    public int get(int key) {
        return lru.get(key);
    }

    public void put(int key, int value) {
        lru.put(key, value);
    }

    public static void main(String[] args) {
          LRUCache obj = new LRUCache(5);
         int param_1 = obj.get(1);
        obj.put(1,1);
    }
}

class LRU extends java.util.LinkedHashMap<Integer, Integer> {

    private int capacity;

    public LRU(int capacity, float load) {
        super(capacity, load);
        this.capacity = capacity;
    }

    @Override
    public boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}
