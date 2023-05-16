package cn.fighter3.holder;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Date: 2023/5/16 1:23</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
public class ChatServerSession {
    private static final ConcurrentHashMap<String, ChannelHandlerContext> sessions = new ConcurrentHashMap<>();

    public void add(ChannelHandlerContext ctx) {
        String id = getId(ctx);
        sessions.put(id, ctx);
    }

    public void remove(ChannelHandlerContext ctx) {
        String id = getId(ctx);
        sessions.remove(id);
    }

    public void send(String to, Object message) {
        ChannelHandlerContext ctx = sessions.get(to);
        if (ctx != null) {
            ctx.writeAndFlush(message);
        } else {
              // 如果接收方不在线，则将消息存储到数据库中
           // messageService.saveMessage(to, message);
        }
    }

    public static String getId(ChannelHandlerContext ctx) {
        return ctx.channel().id().asShortText();
    }

}
