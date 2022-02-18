package ds.guang.majiang.server;


import ds.guang.majiang.server.network.HttpRequestHandler;
import ds.guang.majiang.server.network.codec.IdleStateCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @ClassName GameServer
 * @Description
 * @Author guangmingdexin
 * @Date 2021/3/21 15:28
 * @Version 1.0
 **/
public class GameServer {

    // 监听端口
    private int port;

    public GameServer(int port) {
        this.port = port;
    }

    // 处理客户端的请求
    public void run() {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 创建服务器端的启动对象，配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.group(bossGroup, workerGroup)
                    // 服务器通道实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程连接队列的连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 为 workGroup添加管道，handler
                    // TODO handler 顺序非常重要 先处理心跳信息
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("codec", new HttpServerCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(1024*1024));
                            //
                            pipeline.addLast("string-decoder", new StringDecoder());
                            pipeline.addLast("handler", new HttpRequestHandler());
                            pipeline.addLast("idleCodec", new IdleStateCodec());

//                            pipeline.addLast("ping", new IdleStateHandler(30, 0,
//                                    0, TimeUnit.SECONDS));

                         //   pipeline.addLast("business", new GameServerHandler());
                        }
                    });
            // 启动配置


            // 绑定一个端口并且同步，生成了一个 ChannelFuture 对象
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            System.out.println("服务器, 启动成功！");
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GameServer(9002).run();
    }
}
