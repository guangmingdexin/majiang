package ds.guang.majing.common;

import java.util.Objects;

/**
 * @author guangyong.deng
 * @date 2021-12-13 17:34
 */
public final class ClassUtil {

    public static  <T> T convert(Object obj, Class<T> clazz) {

        Objects.requireNonNull(obj, "convert class don't null");

        try {
            return (T)obj;
        }catch (ClassCastException e) {
            // 打印
            System.out.println("类型转换错误！");
        }
        return null;
    }
}
