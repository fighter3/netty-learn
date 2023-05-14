package cn.fighter3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <p>Date: 2023/5/14 10:29</p>
 * <p>Author: fighter3</p>
 * <p>Description: Netty服务端Demo</p>
 */
public class NettyServer{
    // 服务器监听的端口号
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    /**
     * 启动Netty服务器
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {
        // 创建boss线程组和worker线程组
        // bossGroup 用于监听客户端的连接请求，将连接请求发送给 workerGroup 进行处理
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // workerGroup 用于处理客户端连接的数据读写
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建 ServerBootstrap 对象，用于启动 Netty 服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 绑定线程池事件组
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 通道初始化回调函数，在启动的时候可以自动调用
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加消息处理器
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });

            // 绑定端口，开始接收客户端请求
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            System.out.println("Netty服务器监听端口："+port);

            // 等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //释放线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建服务器对象，监听端口号为 8888
        NettyServer server = new NettyServer(8888);
        System.out.println("============Netty服务器启动...=============");
        // 启动服务器
        server.run();
        System.out.println("============Netty服务器停止...=============");
    }
}
