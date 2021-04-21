package com.guang.majiangclient.client.common;

import java.lang.annotation.*;

/**
 * @author guangmingdexin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Action {
    boolean isAction() default true;
    Event event() default Event.DEFAULTEVENT;
}
