package com.guang.majiangclient.client.util;

import com.guang.majiangclient.client.common.*;
import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.annotation.Package;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.handle.action.ActionFactory;
import com.guang.majiangclient.client.handle.service.*;
import com.guang.majiangclient.client.GameClientThread;
import org.apache.ibatis.io.Resources;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConfigOperation
 * @Description 配置中心
 * @Author guangmingdexin
 * @Date 2021/3/23 9:49
 * @Version 1.0
 **/
public final class ConfigOperation {

    // 默认加载路径
    public final static String CLIENT_CONFIG =  "client-config";
    /**
     * 'c', 10
     * 'b', 100
     * 'd', 1000
     */
    public final static Map<Integer, String> NUM_TO_IMAGE = new HashMap<>();

    public static Map<String, Object> config;

    static {
        for (int i = 11; i <= 19 ; i++) {
            NUM_TO_IMAGE.put(i, "character" + (i % 10) + ".png");
        }
        for (int i = 101; i <= 109; i++) {
            NUM_TO_IMAGE.put(i, "bamboo" + (i % 100) + ".png");
        }
        for (int i = 1001; i <= 1019; i++) {
            NUM_TO_IMAGE.put(i, "dot" + (i % 1000) + ".png");
        }
        InputStream stream;
        try {
            stream = Resources.getResourceAsStream(CLIENT_CONFIG);
            config = loadConfigYaml(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 获取服务中心
        Service center = getCenter();

        // 注册服务
        center.register(new ServiceUIHandler(Event.UIEVENT));
        GameClientThread client = new GameClientThread();
        client.start();
        // 获取 Channel
        center.register(new DefaultServiceHandler());
        // 注册工厂
        // 注册所有的消息实体类
        // packageName 应该使用配置文件处理
        MessageFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangclient.server.message",
                Package.class, true));

        ActionFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangclient.server.handle.action",
                Action.class, true));
    }

    public static void configInit() {

    }

    public static Service getCenter() {
        return ServiceCenter.getInstance();
    }

    public static List<String> numToStrs(List<Integer> nums) {
        List<String> strs = new ArrayList<>();
        for (Integer num : nums) {
            String e = NUM_TO_IMAGE.get(num);
            if(e == null) {
                throw new IllegalArgumentException("数字卡片和图片名称不配合！");
            }
            strs.add(e);
        }
        return strs;
    }

    public static String numToStr(Integer num) {
        String e = NUM_TO_IMAGE.get(num);
        if(e == null) {
            throw new IllegalArgumentException("数字卡片和图片名称不配合！");
        }
        return e;
    }

    private static Map<String, Object> loadConfigYaml(InputStream file) {
        Yaml yaml = new Yaml();
        return yaml.load(file);
    }
}
