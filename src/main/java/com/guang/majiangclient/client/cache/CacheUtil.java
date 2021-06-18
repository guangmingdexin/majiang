package com.guang.majiangclient.client.cache;

import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.entity.GameUser;
import com.guang.majiangclient.client.entity.User;

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

    private static Direction cur = null;

    private static LRU<Direction, GameUser> lru = new LRU<>(4, 0.75F, true);
    /**
     * 游戏过程中进行 特殊事件判断
     */
    private static boolean specialEvent = false;

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
        lru.put(key, value);
    }

    public static GameUser getCacheGameUser(Direction key) {
        return lru.get(key);
    }

    public static Direction getCurDire() {
        return cur;
    }

    public static void setCurDire(Direction dire) {
        cur = dire;
    }

    public static void next(Direction direction) {
        lru.forEach((key, value) -> {
            value.getGameInfoCard().setAroundPlayerDire(direction);
        });
        CacheUtil.setCurDire(direction);
        System.out.println("next-lru: " + lru);
    }

    public static boolean around() {
        return cur == gameUserCache.getDirection();
    }

    public static LRU<Direction, GameUser> getGameUsers() {
        return lru;
    }

    public static boolean getSpecialEvent() {
        return specialEvent;
    }

    public static void setSpecialEvent(boolean event) {
        specialEvent = event;
    }


   public static class LRU<T, K> extends LinkedHashMap<T, K> {

        private int capacity;

        LRU(int initialCapacity, float loadFactor, boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
            this.capacity = initialCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > capacity;
        }
    }

}
