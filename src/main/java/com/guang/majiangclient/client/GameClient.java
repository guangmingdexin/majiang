package com.guang.majiangclient.client;

import com.guang.majiangclient.client.handle.codec.ClientInHandle;
import com.guang.majiangclient.client.handle.codec.GenericPackageCodec;
import com.guang.majiangclient.client.handle.out.GameClientOutHandle;
import com.guang.majiangserver.handle.decodec.GenericPackageClassDecoder;
import com.guang.majiangserver.handle.decodec.GenericPackageDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

/**
 * @ClassName GameClient
 * @Description 客户端
 * @Author guangmingdexin
 * @Date 2021/3/23 8:58
 * @Version 1.0
 **/
public class GameClient {

    // 端口 + ip 地址
    private final int port;

    private final String host;

    @Getter
    private volatile static Channel channel;


    public GameClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void run() {
        // 客户端需要一个事件循环组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        // 创建 客户端启动对象
        Bootstrap bootstrap = new Bootstrap();

        // 设置相关参数
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                // handler childHandler 区别 handler在初始化时就会执行，而childHandler会在客户端成功connect后才执行
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        // 加入解码器
                        // 加入编码器

                        pipeline.addLast("decoder", new GenericPackageDecoder());
                        pipeline.addLast("classDecoder", new GenericPackageClassDecoder());
                        pipeline.addLast("client", new ClientInHandle());

                        pipeline.addLast("encoder", new GenericPackageCodec());
                        pipeline.addLast("connect", new GameClientOutHandle());
                    }
                });

        System.out.println("客户端 ok ..");

        // 启动 客户端连接 服务器
        // 关于 ChannelFuture 涉及到 netty 的异步模型
        try {
            // 通过调用sync同步方法阻塞直到绑定成功
            // 等待异步操作执行完毕
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();

            GameClient.channel = channel;
            System.out.println("--------" + channel.remoteAddress() + "----------");

            // 对关闭通道进行 监听
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  finally {
            eventExecutors.shutdownGracefully();
        }
    }

}
