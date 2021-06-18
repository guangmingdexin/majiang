package com.guang.majiangclient.client.util;

import com.guang.majiangserver.config.ConfigOperation;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName JedisUtil
 * @Description 操作 redis 的工具类
 * @Author guangmingdexin
 * @Date 2021/4/18 9:08
 * @Version 1.0
 **/
public final class JedisUtil {

    private static JedisPool jp;

   static {
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
       jp = new JedisPool(jpc, (String) defaultYaml.get("host"), (int)defaultYaml.get("port"));
   }

    private JedisUtil() {


    }



    public static String get(String key) {
        try (Jedis jedis = jp.getResource()) {
            return jedis.get(key);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String set(String key, String value) {
        try (Jedis jedis = jp.getResource()) {
            return jedis.set(key, value);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Long zadd(String key, double score, String value) {
        try (Jedis jedis = jp.getResource()) {
           return jedis.zadd(key, score, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static int zincrby(String key, double incr, String value) {
        try (Jedis jedis = jp.getResource()) {
            return (int) (jedis.zincrby(key, incr, value) + 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Integer.MIN_VALUE;
    }

    public static Long del(String key) {
        try (Jedis jedis = jp.getResource()) {
            return jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static Transaction multi() {
        try (Jedis jedis = jp.getResource()) {
            return jedis.multi();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean exist(String key) {
        try (Jedis jedis = jp.getResource()) {
            return jedis.exists(key);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Set<String> zrevrange(String key, int start, int end) {
        try (Jedis jedis = jp.getResource()) {
            return jedis.zrevrange(key, start, end);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
