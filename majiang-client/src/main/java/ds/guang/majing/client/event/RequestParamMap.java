package ds.guang.majing.client.event;

import java.util.HashMap;

/**
 * @author guangmingdexin
 */
public class RequestParamMap<K, V> extends HashMap<K, V> {

    public RequestParamMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
}
