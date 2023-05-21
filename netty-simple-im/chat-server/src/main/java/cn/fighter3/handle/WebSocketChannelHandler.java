package cn.fighter3.handle;

import cn.fighter3.manager.ChannelManager;
import cn.fighter3.model.Message;
import cn.fighter3.service.ChatService;
import cn.fighter3.util.JacksonUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Date: 2023/5/16 1:40</p>
 * <p>Author: fighter3</p>
 * <p>Description: Websocket channel处理器</p>
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class WebSocketChannelHandler extends SimpleChannelInboundHandler<Object> {

    public static final AttributeKey<String> USER_ID = AttributeKey.valueOf("userId");

    @Autowired
    private ChatService chatService;

    /**
     * 消息阅读处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        log.info("[websocket] channelRead0:{}",msg);
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
            Message message= JacksonUtil.parseObject(textWebSocketFrame.text(),Message.class);
            log.info("[channelRead0] message:{}",textWebSocketFrame.text());
            chatService.handleMessage(channelHandlerContext.channel(),message);
        }
    }


    /**
     * 处理连接事件
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active: {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel inactive: {}", ctx.channel());
        ChannelManager.removeChannel(ctx.channel());
    }
}
