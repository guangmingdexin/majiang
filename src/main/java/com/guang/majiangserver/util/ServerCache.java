package com.guang.majiangserver.util;

import com.guang.majiangclient.client.util.ImageUtil;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangserver.config.ConfigOperation;
import com.guang.majiangserver.game.ServerGameLog;
import com.guang.majiangserver.mapper.InfoMapper;
import io.netty.channel.Channel;
import lombok.Getter;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ChannelCache
 * @Author guangmingdexin
 * @Date 2021/5/17 21:41
 * @Version 1.0
 **/
public class ServerCache {

    private final static ConcurrentHashMap<Long, Channel> group = new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<Long, ServerGameLog> logs = new ConcurrentHashMap<>();

    public final static ConcurrentHashMap<String, Long> channelPlayer = new ConcurrentHashMap<>();

    public static Channel getChannel(Long channelId) {
        return group.get(channelId);
    }

    public static void add(Long channelId, Channel channel) {
        group.put(channelId, channel);
    }

    public static void writeLog(long roomId, ServerGameLog log) {
        logs.put(roomId, log);
    }

    public static ServerGameLog readLog(long roomId) {
        return logs.get(roomId);
    }

    public static void add(String channelId, Long userId) {
        channelPlayer.put(channelId, userId);
    }

    public static Long getUserId(String channelId) {
        return channelPlayer.get(channelId);
    }

    public static String channelId(Channel channel) {
        if(channel == null) {
            return "null";
        }else {
            return channel.id().asShortText();
        }
    }

    public static void removeUserId(String channelId) {
        channelPlayer.remove(channelId);
    }

    public static String getAvatar(Long uid) {

        String key = "avatar:" + uid;
        String base64 = JedisUtil.get(key);
        if(base64 != null) {
            return base64;
        }else {
            SqlSessionFactory sqlSessionFactory = ConfigOperation.getSqlSessionFactory();
            SqlSession sqlSession = sqlSessionFactory.openSession();
            InfoMapper mapper = sqlSession.getMapper(InfoMapper.class);
            String path = mapper.getAvatarPath(uid);
            if("default.jpg".equals(path)) {
                base64 = ImageUtil.encoderBase64(path, false);
            }else {
                base64 = ImageUtil.encoderBase64(path, true);
            }
            JedisUtil.set(key, base64);
        }
        return base64;
    }

    public static String getAvatar(String key, String path) {

        String base64 = JedisUtil.get(key);
        if(base64 == null) {
            // 加载头像资源
            // 判断是否为默认头像
            if("default.jpg".equals(path)) {
                base64 = ImageUtil.encoderBase64(path, false);
            }else {
                base64 = ImageUtil.encoderBase64(path, true);
            }
            JedisUtil.set(key, base64);
        }
        return base64;
    }
}
