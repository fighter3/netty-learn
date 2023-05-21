package cn.fighter3.controller;

import cn.fighter3.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Date: 2023/5/16 1:31</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    ChatService chatService;
}
