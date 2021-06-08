package com.guang.majiangserver.config;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.MessageFactory;
import com.guang.majiangclient.client.common.annotation.Package;
import com.guang.majiangclient.client.util.ClassUtil;
import com.guang.majiangserver.handle.action.ActionFactory;
import lombok.Getter;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ConfigOperation
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/9 14:30
 * @Version 1.0
 **/
public class ConfigOperation {

    // 默认加载路径
   private final static String SERVER_CONFIG =  "server-config";

   public static Map config;

   @Getter
   private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            InputStream stream = Resources.getResourceAsStream(SERVER_CONFIG);
            config = loadConfigYaml(stream);
            String mybatisConfig = (String) config.get("mybatis.config");
            InputStream inputStream = Resources.getResourceAsStream(mybatisConfig);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            // 注册所有的消息实体类
            // packageName 应该使用配置文件处理
            MessageFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangclient.client.message",
                    Package.class, true));

            // 注册所有的业务处理类
            ActionFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangserver.handle.action",
                    Action.class, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void init() {}

    public static Map<String, Object> getDefaultRedisYaml() {
       Map<String, Object> redisConfig = new HashMap<>();
        config.forEach((key, value) -> {
            String s = key.toString();
            if(s.startsWith("redis.")) {
                String[] split = s.split("\\.");
                if(split.length > 1) {
                    redisConfig.put(split[split.length - 1], value);
                }
            }
        });

        // 出现异常加载默认配置类
        if(redisConfig.size() < 2) {
            redisConfig.put("host", "127.0.0.1");
            redisConfig.put("port", 6379);
        }

        return redisConfig;
    }


    private static Map loadConfigYaml(InputStream file) {
        Yaml yaml = new Yaml();
        return yaml.load(file);
    }

    public static String getDefaultAvatar() {
       if(config != null) {
           return config.get("avatar") + File.separator + "default.jpg";
       }
       return "default.jpg";
    }


}
