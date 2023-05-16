package cn.fighter3.controller;

import cn.fighter3.model.ChatMessage;
import cn.fighter3.model.ConfirmMessage;
import cn.fighter3.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    // ...
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody ChatMessage message, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        message.setFrom(String.valueOf(userId));
        chatService.send(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmMessage message, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        message.setFrom(String.valueOf(userId));
        chatService.confirm(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
