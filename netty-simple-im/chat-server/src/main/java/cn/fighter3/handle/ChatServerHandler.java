package cn.fighter3.handle;

import cn.fighter3.holder.ChatServerSession;
import cn.fighter3.model.ChatMessage;
import cn.fighter3.model.ConfirmMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

/**
 * <p>Date: 2023/5/16 1:27</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Component
@ChannelHandler.Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<Object> {
    // ...
    private final ChatServerSession session;

    public ChatServerHandler(ChatServerSession session) {
        this.session = session;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 处理聊天请求
        if (msg instanceof ChatMessage) {
            ChatMessage chatMessage = (ChatMessage) msg;
            String to = chatMessage.getTo();
            session.send(to, chatMessage);
            return;
        }

        // 处理确认消息
        if (msg instanceof ConfirmMessage) {
            ConfirmMessage confirmMessage = (ConfirmMessage) msg;
            String to = confirmMessage.getTo();
            session.send(to, confirmMessage);
            return;
        }

        super.channelRead(ctx, msg);
    }
}
