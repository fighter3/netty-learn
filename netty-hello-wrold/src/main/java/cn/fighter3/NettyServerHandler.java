package cn.fighter3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * <p>Date: 2023/5/14 10:30</p>
 * <p>Author: fighter3</p>
 * <p>Description: Netty服务器消息处理器</p>
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端上线的时候会触发这个方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String message="你好，靓仔！";
        ByteBuf hello = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
        // 发送消息
        ctx.writeAndFlush(hello);
    }

    /**
     *当 Channel 中有来自客户端的数据时就会触发这个方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发来的消息：" + buf.toString(CharsetUtil.UTF_8)); // 接收消息并打印输出
    }

    /**
     * 当有异常时触发这个方法
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

