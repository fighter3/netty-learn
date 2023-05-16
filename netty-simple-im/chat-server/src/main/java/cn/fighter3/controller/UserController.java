package cn.fighter3.controller;

import cn.fighter3.model.LoginRequest;
import cn.fighter3.model.LoginResponse;
import cn.fighter3.model.User;
import cn.fighter3.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Date: 2023/5/16 1:30</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@RestController
@RequestMapping("/user")
public class UserController implements Serializable {

    @Autowired
    UserService userService;
    // ...
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        LoginResponse loginResponse=userService.login(request);
        if (loginResponse==null || loginResponse.getUser()==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user =loginResponse.getUser();

        session.setAttribute("userId", user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", UUID.randomUUID().toString());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
