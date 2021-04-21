package com.guang.majiangclient.client.common;

import java.lang.annotation.*;

/**
 * @author guangmingdexin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Package {
    MessageVersion version() default MessageVersion.V10;
    MessageType type();
}
