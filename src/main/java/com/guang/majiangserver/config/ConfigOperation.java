package com.guang.majiangserver.config;

import com.guang.majiang.common.ImageRoot;
import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.common.MessageFactory;
import com.guang.majiangclient.client.common.Package;
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
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/9 14:30
 * @Version 1.0
 **/
public class ConfigOperation {

    // 默认加载路径
   public final static String PATH = ImageRoot.GAME_CLIENT_CONFIG.getPath() + File.separator + "server-config";

   private static Map config;

   @Getter
   private static SqlSessionFactory sqlSessionFactory;

   public static void init() {


       String classpath = "config/mybatis/xml/mybatis-config";
        // 加载配置类
        try {
            config = loadConfigYaml(new FileInputStream(PATH));
            InputStream inputStream = Resources.getResourceAsStream(classpath);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            System.out.println(sqlSessionFactory.getConfiguration().getMapperRegistry().getMappers());
        } catch (IOException e) {
            e.printStackTrace();
        }
       // 注册所有的消息实体类
        // packageName 应该使用配置文件处理
        MessageFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangclient.client.message",
                Package.class, true));

        // 注册所有的业务处理类
       ActionFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangserver.handle.action",
               Action.class, true));

    }

    public static Map<String, Object> getDefaultRedisYaml() {
       Map<String, Object> redisConfig = new HashMap<>();
        try {
            Map map = loadConfigYaml(new FileInputStream(PATH));
            map.forEach((key, value) -> {
                String s = key.toString();
                if(s.startsWith("redis.")) {
                    String[] split = s.split("\\.");
                    if(split.length > 1) {
                        redisConfig.put(split[split.length - 1], value);
                    }
                }
            });
            return redisConfig;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 出现异常加载默认配置类
        redisConfig.put("host", "127.0.0.1");
        redisConfig.put("port", 6379);

        return redisConfig;
    }

    public static Map loadConfigYaml(FileInputStream file) {
        Yaml yaml = new Yaml();
        return yaml.load(file);
    }

    public static String getDefaultAvatar() {
       if(config != null) {
           return config.get("avatar") + File.separator + "default.jpg";
       }
       return "default.jpg";
    }


    public static void main(String[] args) throws IOException {
        InputStream resourceAsStream = ConfigOperation.class.getResourceAsStream("/config/mybatis/xml/1");
        System.out.println(resourceAsStream.available());
    }

}
