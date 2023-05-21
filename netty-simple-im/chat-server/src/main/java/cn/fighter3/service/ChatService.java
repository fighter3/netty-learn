package cn.fighter3.service;

import cn.fighter3.model.Message;
import io.netty.channel.Channel;

/**
 * <p>Date: 2023/5/16 1:37</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
public interface ChatService {

    /**
     * 消息处理入口
     */
    void handleMessage(Channel channel, Message message);

    /**
     * 处理用户上线消息
     */
    void handleOnlineMessage(Channel channel, Message message);

    /**
     * 处理用户下线消息
     */
    void handleOfflineMessage(Channel channel, Message message);

    /**
     * 处理单聊消息
     */
    void handleSingleChat(Channel channel,Message message);

    /**
     * 处理群聊消息
     */
    void handleGroupChat(Channel channel, Message message);
}
