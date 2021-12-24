package ds.guang.majiang.server.layer;

import java.lang.annotation.*;

/**
 * @author guangyong.deng
 * @date 2021-12-23 15:26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface StateMatchAction {

    String value();

    int order() default 1;

    boolean use() default true;
}
