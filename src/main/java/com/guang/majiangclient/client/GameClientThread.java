package com.guang.majiangclient.client;

import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.handle.event.ServerLinkedError;
import com.guang.majiangclient.client.handle.service.Service;
import com.guang.majiangclient.client.util.ConfigOperation;

/**
 * @ClassName GameClientThread
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/24 21:02
 * @Version 1.0
 **/
public class GameClientThread extends Thread {

    private Service center;

    public GameClientThread() {
        this.center = ConfigOperation.getCenter();
    }

    @Override
    public void run() {
        // 从配置文件读取 服务器 端口， ip 地址
        if(ConfigOperation.config == null) {
            throw new NullPointerException("读取配置文件失败！");
        }
        ServerConfig serverConfig = new ServerConfig(ConfigOperation.config);
        // 通过事件队列进行线程之间的通信
        // 1.用户启动客户端
        // 2.连接远程服务器
        // 2.1 连接失败，提示信息
        // 2.2 连接成功，传入 用户 mac 地址，查询远程服务器中是否存在该用户
        // 如果存在 则返回用户基本信息
        // 如果不存在 则创建用户
        // 启动 客户端线程
        GameClient client = new GameClient(serverConfig.getPort(),
                serverConfig.getHost());
        try {
            client.run();
        }catch (Exception e) {
            center.submit(new ServerLinkedError());
        }
    }

}
