package com.guang.majiangclient.client.common.annotation;

import com.guang.majiangclient.client.common.enums.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记注解，有此注解说明某个任务为 哪种事件
 *
 * @author 光明的心
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RunnableEvent {

    Event value();
}
