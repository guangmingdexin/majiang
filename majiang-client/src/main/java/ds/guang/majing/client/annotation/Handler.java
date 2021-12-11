package ds.guang.majing.client.annotation;

import java.lang.annotation.*;

/**
 * @author guangyong.deng
 * @date 2021-12-10 15:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Handler {

    boolean use() default true;

}
