package cn.fighter3.service;

import cn.fighter3.handle.NettyChannelManager;
import cn.fighter3.handle.WebSocketServerHandler;
import cn.fighter3.model.ChatMessage;
import cn.fighter3.model.ConfirmMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Date: 2023/5/16 1:37</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Service
@Slf4j
public class ChatService {
    private final WebSocketServerHandler handler;
    private final UserService userService;

    // 存储在线用户与其对应的 WebSocket 通道 ID
    private final Map<String, ChannelId> onlineUserMap = new ConcurrentHashMap<>();
    private final Map<ChannelId,Channel> channelMap=new ConcurrentHashMap<>();

    @Autowired
    public ChatService(WebSocketServerHandler handler, UserService userService) {
        this.handler = handler;
        this.userService = userService;
    }

    /**
     * 处理聊天消息
     */
    public void handleMessage(ChatMessage message) {
        String from = message.getFrom();
        String to = message.getTo();
        ChannelId channelId = onlineUserMap.get(to);
        if (channelId != null) {
            ConfirmMessage confirmMessage = new ConfirmMessage();
            confirmMessage.setId(message.getId());
            confirmMessage.setFrom(from);
            confirmMessage.setTo(to);

            this.sendMessage(channelId, message);
            this.sendMessage(channelId, confirmMessage);
        }
    }

    /**
     * 确认收到消息
     */
    public void handleConfirm(ConfirmMessage message) {
        String to = message.getTo();
        ChannelId channelId = onlineUserMap.get(to);
        if (channelId != null) {
            this.sendMessage(channelId, message);
        }
    }

    /**
     * 添加用户至在线列表
     */
    public void addUser(String userId, ChannelId channelId) {
        onlineUserMap.put(userId, channelId);
        channelMap.put(channelId, NettyChannelManager.getChannel(channelId));
    }

    /**
     * 从在线列表中移除用户
     */
    public void removeUser(String userId) {
        ChannelId channelId = onlineUserMap.remove(userId);
        if (channelId != null) {
            channelMap.remove(channelId);
        }
    }

    public void sendMessage(ChannelId channelId, Object message) {
        Channel channel = channelMap.get(channelId);
        if (channel != null) {
            Attribute<String> attrUserId = channel.attr(WebSocketServerHandler.USER_ID);
            String userId = attrUserId.get();
            log.info(String.format("向用户[%s]发送消息：%s", userId, message));
            handler.sendMessage(channel, message);
        } else {
            log.error(String.format("无法找到 ChannelId=[%s] 的用户", channelId.asShortText()));
        }
    }

    public void send(ChatMessage message) {
        // 创建确认消息
        ConfirmMessage confirmMessage = new ConfirmMessage();
        confirmMessage.setId(message.getId());
        confirmMessage.setType("send");

        // 发送消息到指定用户
        String userId = message.getTo();
        ChannelId channelId = onlineUserMap.get(userId);
        if (channelId == null) {
            log.warn(String.format("无法找到用户[%s]的连接", userId));
            return;
        }
        sendMessage(channelId, message);

        // 发送确认消息给发送方
        String fromUserId = message.getFrom();
        ChannelId fromChannelId = onlineUserMap.get(userId);
        if (fromChannelId != null) {
            sendMessage(fromChannelId, confirmMessage);
        }
    }

    public void confirm(ConfirmMessage message) {
        // 根据确认消息中的 ID 查找对应的消息，并将其删除
        String id = message.getId();
        ChatMessage chatMessage = getMessageQueue().remove(id);
        if (chatMessage == null) {
            logger.warn(String.format("无法找到 ID=[%s] 的消息", id));
            return;
        }

        // 发送确认消息到指定用户
        String userId = chatMessage.getFrom();
        ChannelId channelId = getUserIdToChannelIdMap().get(userId);
        if (channelId == null) {
            log.warn(String.format("无法找到用户[%s]的连接", userId));
            return;
        }
        sendMessage(channelId, message);
    }


}
