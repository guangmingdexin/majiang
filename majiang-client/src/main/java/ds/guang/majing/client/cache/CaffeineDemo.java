package ds.guang.majing.client.cache;

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

        int start = 0b1100;

      //  System.out.println("0b" + Integer.toBinaryString(12));

     //   System.out.println("0b" + Integer.toBinaryString(start));
        // 第 4 段 ，
        int offset = (start << 2);
        System.out.println("0b" + Integer.toBinaryString(offset) + "--" + offset);

        // System.out.println("0b" + Long.toBinaryString(64L));
        long mask = 0xfL << offset;
        System.out.println("0b" + Long.toBinaryString(mask) + "----" + mask);

        long result = (1L << offset);
        System.out.println("0b" + Long.toBinaryString(result) + result);
        // 流程模拟 假设 start = 0b1000 正好为 8
        //  index 不同的下标假设 为 5
        // start << 2 相当于 64 位中的下标 （这个怎么理解）
        // 64 = 100 0000
        // 0000 0000 0000 0000 0000 0000 0000 0000
        // 2^6

    }

    public static void main(String[] args) {

    }
}
