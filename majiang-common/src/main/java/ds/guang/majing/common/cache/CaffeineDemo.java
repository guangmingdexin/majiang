package ds.guang.majing.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.UUID;

/**
 * @author guangyong.deng
 * @date 2021-12-24 13:58
 */
public class CaffeineDemo {


    public static void caffeineTest() {

        Cache<String, Object> cache = Caffeine
                .newBuilder()
                //设置缓存的 Entries 个数最多不超过1000个
                .maximumSize(1000)
                .build();
    }

    public static void countMinSketchDemo() {

        int hash = UUID.randomUUID().toString().hashCode();

        int start = 0b1000;

        System.out.println("0b" + Integer.toBinaryString(start));

        System.out.println("0b" + Integer.toBinaryString(start << 2));

        // 流程模拟 假设 start = 0b1000 正好为 8
        //  index 不同的下标假设 为 5

    }

    public static void main(String[] args) {
        countMinSketchDemo();
    }
}
