package com.guang.majiangclient.client.common.annotation;

import java.lang.annotation.*;

/**
 * @author guangmingdexin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ClassInfo {
    boolean isDeserializer() default false;
}
