package cn.fighter3.handle;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * <p>Date: 2023/5/19 22:05</p>
 * <p>Author: fighter3</p>
 * <p>Netty服务器，利用CommandLineRunnerSpringBoot项目启动时启动</p>
 */
@Component
@Slf4j
public class NettyServerRunner implements CommandLineRunner {
    @Autowired
    private WebSocketChannelHandler webSocketChannelHandler;
    /**
     * netty-server启动端口号
     */
    @Value("${netty.port}")
    private Integer port;

    @Override
    public void run(String... args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(webSocketChannelHandler);
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("Netty server started on port {}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Netty server start error:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
