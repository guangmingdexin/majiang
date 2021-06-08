package com.guang.majiangclient.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @ClassName ServerConfig
 * @Description
 * @Author guangmingdexin
 * @Date 2021/3/24 21:05
 * @Version 1.0
 **/
@Getter
@Setter
public class ServerConfig {

    private int port;

    private String host;

    public ServerConfig() {
        this.port = 7000;
        this.host = "127.0.0.1";
    }

    public ServerConfig(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public ServerConfig(InetSocketAddress address) {
        this.port = address.getPort();
        this.host = address.getHostName();
    }

    public ServerConfig(Map<String, Object> map) {
        // 判断 ip 是否存在
        String ip = map.get("server-ip").toString();
        String port = map.get("server-port").toString();
        if(ip == null || port == null) {
            throw new NullPointerException("配置文件中没有 ip 地址 或者没有端口！");
        }
        this.host = ip;
        this.port = Integer.parseInt(port);
    }
}
