package cn.fighter3.handle;

import cn.fighter3.holder.ChatServerSession;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * <p>Date: 2023/5/16 1:29</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
    // ...
    private final ChatServerSession session;
    private final MessageService messageService;

    public ChatServerInitializer(ChatServerSession session, MessageService messageService) {
        this.session = session;
        this.messageService = messageService;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        ch.pipeline().addLast(new ObjectEncoder());
        ch.pipeline().addLast(new ChatServerHandler(session));
    }
}
