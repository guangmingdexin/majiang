package com.guang.majiangclient.client.cache;

import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.entity.GameUser;
import com.guang.majiangclient.client.entity.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName CacheUtil
 * @Description 本地内存
 * @Author guangmingdexin
 * @Date 2021/4/16 10:33
 * @Version 1.0
 **/
public final class CacheUtil {

    private static User userCache = null;

    private static GameUser gameUserCache = null;

    private static LRU<Direction, GameUser> avatarUserCache = new LRU<>(4, 0.75F, true);

    public static User getUserInfo() {
        return userCache;
    }

    public static GameUser getGameUserInfo() {
        return gameUserCache;
    }

    public static void addGameUser(GameUser gameUser) {
        gameUserCache = gameUser;
    }

    public static void addUserInfo(User user) {
        userCache = user;
    }

    public static void addCacheGameUser(Direction key, GameUser value) {
        avatarUserCache.put(key, value);
    }

    public static GameUser getCacheGameUser(Direction key) {
        return avatarUserCache.get(key);
    }


    public static LRU<Direction, GameUser> getGameUsers() {
        return avatarUserCache;
    }

   public static class LRU<T, K> extends LinkedHashMap<T, K> {

        private int capacity;

        public LRU(int initialCapacity, float loadFactor, boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
            this.capacity = initialCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > capacity;
        }
    }
}
