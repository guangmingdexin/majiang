package com.guang.majiangclient.client.common;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName MessageFactory
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/6 9:42
 * @Version 1.0
 * @Descrip 通过 消息工厂 生成对应的 消息对象
 *
 **/
public class MessageFactory {

    private static final Set<Class<?>> factory = ConcurrentHashMap.newKeySet();

    public static void register(Class<?> clazz) {

        if(factory.contains(clazz)) {
            return;
           // throw new IllegalArgumentException("重复注册 bean!");
        }
        // 通过 class 反射构造对象，得到 javabean

        // c/s 在解码的过程中，需要通过 version 和 type 获取到 对应的 class 对象
        // 1. 方案 构造一个 Type 对象 作为 key
        // 2. 方案 想办法将 version 和 type 进行 组合 作为 key
        // 3. 方案 使用 redis key : version:type value : class
        factory.add(clazz);

    }

    public static void registerAll(Set<Class<?>> classes) {
        factory.addAll(classes);
    }

    public static Class<?> getClass(short version, short type) {

        // 遍历 工厂类
        for (Class<?> clazz : factory) {
            // 获取 注解
            Package annotation = clazz.getAnnotation(Package.class);
            if(annotation != null && annotation.version().getVersion() == version &&
                    annotation.type().getType() == type) {
                return clazz;
            }
        }
        return null;
    }
}
