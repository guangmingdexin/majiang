package com.guang.majiangclient.client.cache;

import com.guang.majiangclient.client.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName CacheUtil
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/16 10:33
 * @Version 1.0
 **/
public final class CacheUtil {

    private static Map<Long, User> cache = new ConcurrentHashMap<>();

    public static User getUserInfo(long id) {
        return cache.get(id);
    }

    public static User addUserInfo(User user) {

        return cache.put((long) user.getUserId(), user);
    }
}
