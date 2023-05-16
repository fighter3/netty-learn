package cn.fighter3.handle;

import cn.fighter3.model.ChatMessage;
import cn.fighter3.model.ConfirmMessage;
import cn.fighter3.model.LoginRequest;
import cn.fighter3.model.LoginResponse;
import cn.fighter3.service.ChatService;
import cn.fighter3.service.UserService;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>Date: 2023/5/16 1:40</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Component
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private final UserService userService;
    private final ChatService chatService;

    private WebSocketServerHandshaker handshaker;

    public static final AttributeKey<String> USER_ID = AttributeKey.valueOf("userId");

    @Autowired
    public WebSocketServerHandler(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    // 处理 HTTP 请求
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (request.decoderResult().isFailure()) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        if (request.method() != HttpMethod.GET || !"/websocket".equals(request.uri())) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketURL(request), null, true);
        handshaker = wsFactory.newHandshaker(request);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }

    // 处理 WebSocket 消息
    private void handleWebSocketMessage(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("不支持的消息类型：" + frame.getClass().getName());
        }

        String message = ((TextWebSocketFrame) frame).text();
        handleMessage(ctx.channel(), message);
    }

    // 处理消息
    private void handleMessage(Channel channel, String message) {
        Object obj = JSON.parse(message);
        if (obj instanceof ChatMessage) {
            chatService.handleMessage((ChatMessage) obj);
        } else if (obj instanceof ConfirmMessage) {
            chatService.handleConfirm((ConfirmMessage) obj);
        } else if (obj instanceof LoginRequest) {
            LoginRequest request = (LoginRequest) obj;
            LoginResponse response = userService.login(request);
            if (response.getCode() == 200) {
                String userId = response.getUser().getId();
                channel.attr(USER_ID).set(userId);
                chatService.addUser(userId, channel.id());
            }
            sendMessage(channel, response);
        }
    }

    // 发送消息
    public void sendMessage(Channel channel, Object message) {
        String json = JSON.toJSONString(message);
        channel.write(new TextWebSocketFrame(json));
        channel.flush();
    }

    // 发送 HTTP 响应
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse response) {
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
        }

        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    // 获取 WebSocket URL
    private static String getWebSocketURL(FullHttpRequest request) {
        String location = request.headers().get(HttpHeaderNames.HOST) + "/websocket";
        return "ws://" + location;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端已连接：" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userId = ctx.channel().attr(USER_ID).get();
        if (userId != null) {
            chatService.removeUser(userId);
            log.info("用户已下线：" + userId);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketMessage(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常发生，关闭连接：" + ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
