package cn.fighter3.model;

import cn.fighter3.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>Date: 2023/5/17 9:55</p>
 * <p>Author: fighter3</p>
 * <p>Description: 消息基本类</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    /**
     * 消息类型
     */
    private MessageType messageType;
    /**
     * 发送者
     */
    private String fromUserId;
    /**
     * 接受者
     */
    private String toUserId;
    /**
     * 消息内容
     */
    private String content;
}
