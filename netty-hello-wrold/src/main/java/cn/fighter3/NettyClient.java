package cn.fighter3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * <p>Date: 2023/5/14 10:32</p>
 * <p>Author: fighter3</p>
 * <p>Description: Netty客户端Demo</p>
 */
public class NettyClient {
    // 服务器 IP
    private String host;
    // 服务器监听的端口号
    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 启动 Netty 客户端
     */
    public void run() throws InterruptedException {
        // 创建事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            // 配置 Bootstrap 对象
            // 设置线程组
            bootstrap.group(group)
                    // 设置客户端通信的通道类型为NIO类型
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        // 通道初始化回调函数，在启动的时候可以自动调用
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 添加消息处理器
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            // 连接服务器，异步等待连接成功
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            System.out.println("===========Netty客户端连接服务端=========");

            // 等待客户端连接关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //释放资源
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
       // 创建客户端对象，并连接到服务器
        NettyClient client = new NettyClient("127.0.0.1", 8888);
        // 启动客户端，开始发送消息
        client.run();
    }
}
