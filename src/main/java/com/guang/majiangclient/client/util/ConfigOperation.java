package com.guang.majiangclient.client.util;

import com.guang.majiang.common.ImageRoot;
import com.guang.majiangclient.client.GameClient;
import com.guang.majiangclient.client.common.*;
import com.guang.majiangclient.client.common.Package;
import com.guang.majiangclient.client.handle.action.ActionFactory;
import com.guang.majiangclient.client.service.Service;
import com.guang.majiangclient.client.service.ServiceCenter;
import com.guang.majiangclient.client.service.ServiceRegisterHandler;
import com.guang.majiangclient.client.service.ServiceUIHandler;
import com.guang.majiangclient.client.thread.GameClientThread;
import io.netty.channel.Channel;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
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
    final static String PATH = ImageRoot.GAME_CLIENT_CONFIG.getPath() + File.separator + "client-config";

    public static ServerConfig getServerConfig() {
        return config(PATH);

    }

    public static ServerConfig getServerConfig(String myPath) {
        if(myPath == null) {
            return config(PATH);
        }
        return config(myPath);
    }

    private static ServerConfig config(String path) {
        ServerConfig config = null;
        try {
            FileInputStream in = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line = null;
            Map<String, String> map = new HashMap<>();
            String match = "^[A-Za-z].*";

            while ((line = reader.readLine()) != null) {
                if(!line.matches(match)){
                    continue;
                }
                String[] strs = line.split(":");
                String key = strs[0].replace(" ", "");
                String value = strs[1].replace(" ", "");
                map.put(key, value);
            }
            in.close();
            reader.close();
            config = new ServerConfig(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

    public static void configInit() {
        // 获取服务中心
        Service center = getCenter();

        // 注册服务
        center.register(new ServiceUIHandler(Event.UIEVENT));
        GameClientThread client = new GameClientThread();
        client.start();
        // 获取 Channel
        center.register(new ServiceRegisterHandler(Event.REGISTER));
        // 注册工厂
        // 注册所有的消息实体类
        // packageName 应该使用配置文件处理
        MessageFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangclient.client.message",
                Package.class, true));

        ActionFactory.registerAll(ClassUtil.getClassFromPath("com.guang.majiangclient.client.handle.action",
                Action.class, true));
    }

    public static Service getCenter() {
        return ServiceCenter.getInstance();
    }
}
