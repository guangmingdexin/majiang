package ds.guang.majing.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * @author guangmingdexin
 */
public final class CaffeineCache {

    private Cache<String, Object> cache;

    public CaffeineCache() {
        cache = Caffeine
                .newBuilder()
                //设置缓存的 Entries 个数最多不超过1000个
                .maximumSize(1000)
                .build();
    }
}
