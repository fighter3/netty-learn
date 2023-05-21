package cn.fighter3.enums;

import lombok.Data;

/**
 * <p>Date: 2023/5/17 9:55</p>
 * <p>Author: fighter3</p>
 * <p>Description: 消息类型</p>
 */
public enum MessageType {
    /**
     * 用户上线消息
     */
    ONLINE,
    /**
     * 用户下线消息
     */
    OFFLINE,
    /**
     * 单聊消息
     */
    SINGLE_CHAT,
    /**
     * 群聊消息
     */
    GROUP_CHAT;
}
