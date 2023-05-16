package cn.fighter3.model;

import lombok.Data;

/**
 * <p>Date: 2023/5/16 1:33</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 * ChatMessage 类用于封装聊天消息。它包含了 id、from、to、content、timestamp 和 confirmed 属性，分别表示消息 ID、发送者 ID、接收者 ID、消息内容、时间戳和确认标志。
 */
@Data
public class ChatMessage {
    private static final long serialVersionUID = -1L;

    private String id;
    private String from;
    private String to;
    private String content;
    private long timestamp;
    private boolean confirmed;
}
