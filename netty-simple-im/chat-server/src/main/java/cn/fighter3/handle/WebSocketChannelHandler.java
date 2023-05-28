package cn.fighter3.handle;

import cn.fighter3.enums.MessageType;
import cn.fighter3.manager.ChannelManager;
import cn.fighter3.manager.UserManager;
import cn.fighter3.model.Message;
import cn.fighter3.model.User;
import cn.fighter3.service.ChatService;
import cn.fighter3.util.JacksonUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;
import java.util.List;

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

    public static final AttributeKey<String> TOKEN = AttributeKey.valueOf("token");


    @Autowired
    private ChatService chatService;

    @Resource
    private UserManager userManager;
    @Resource
    private ChannelManager channelManager;

    /**
     * 消息阅读处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("[websocket] channelRead0:{}",msg);

        //处理http请求
        if (msg instanceof FullHttpRequest) {
            // 处理HTTP请求
            FullHttpRequest req = (FullHttpRequest) msg;
            if (req.decoderResult().isSuccess() && "websocket".equals(req.headers().get("Upgrade"))) {
                // 处理WebSocket握手请求
                String uri = req.uri();
                String[] paramsArray = uri.split("\\?");
                if (null != paramsArray && paramsArray.length > 1) {
                    String token = paramsArray[1];
                    log.info("提取token成功:{}",token);
                    ctx.channel().attr(TOKEN).set(token);
                }
                //响应客户端
                handleWebSocketHandshake(ctx, req);
            }
        }else if (msg instanceof TextWebSocketFrame) {
            //处理websocket请求
            //先判断一下是否登录
            String token=ctx.channel().attr(TOKEN).get();
            User user=userManager.getUserByToken(token);
            if (user==null){
                throw new AuthenticationException("Invalid token");
            }
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
            Message message= JacksonUtil.parseObject(textWebSocketFrame.text(),Message.class);
            log.info("[channelRead0] message:{}",textWebSocketFrame.text());
            chatService.handleMessage(ctx.channel(),message);
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
        channelManager.removeChannel(ctx.channel());
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof AuthenticationException) {
            log.warn("Authentication failed: {}", cause.getMessage());
            ctx.writeAndFlush(new TextWebSocketFrame("Authentication failed: " + cause.getMessage()))
                    .addListener(ChannelFutureListener.CLOSE);
        } else {
            log.error("[exceptionCaught] ctx:{} \ncause:",ctx,cause);
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * 给客户端的握手请求响应
     */
    private void handleWebSocketHandshake(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 构造WebSocket握手响应
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" + req.headers().get(HttpHeaderNames.HOST), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

}
