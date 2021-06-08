package com.guang.majiangclient.client.common.annotation;

import com.guang.majiangclient.client.common.enums.MessageType;
import com.guang.majiangclient.client.common.enums.MessageVersion;

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
