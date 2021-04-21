package com.guang.majiangclient.client.util;

import com.guang.majiangserver.config.ConfigOperation;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

/**
 * @ClassName JedisUtil
 * @Description 操作 redis 的工具类
 * @Author guangmingdexin
 * @Date 2021/4/18 9:08
 * @Version 1.0
 **/
public final class JedisUtil {

    private static Map<String, Object> config;

   Jedis jedis;

    private JedisUtil() {
        // 读取配置文件 以 redis 开头的配置属性
        Map<String, Object> defaultYaml = ConfigOperation.getDefaultRedisYaml();

        JedisPoolConfig jpc = new JedisPoolConfig();

        Object maxTotal = defaultYaml.get("maxTotal");
        Object idle = defaultYaml.get("idle");
        if(maxTotal == null) {
            maxTotal = 30;
        }
        if(idle == null) {
            idle = 10;
        }
        jpc.setMaxTotal((int) maxTotal);
        jpc.setMinIdle((int) idle);
        JedisPool jp = new JedisPool(jpc, (String) defaultYaml.get("host"), (int)defaultYaml.get("port"));
        jedis = jp.getResource();
    }

    public static Jedis getJedis() {
        return JedisHolder.jedisUtil.jedis;
    }

    static class JedisHolder {
        static JedisUtil jedisUtil = new JedisUtil();
    }

}
