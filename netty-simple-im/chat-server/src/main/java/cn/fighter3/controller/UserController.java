package cn.fighter3.controller;

import cn.fighter3.model.Group;
import cn.fighter3.model.LoginRequest;
import cn.fighter3.model.LoginResponse;
import cn.fighter3.model.User;
import cn.fighter3.service.UserService;
import cn.fighter3.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * <p>Date: 2023/5/16 1:30</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@RestController
@RequestMapping("im/user")
@Slf4j
public class UserController implements Serializable {

    @Autowired
    UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("[login] request:{}", JacksonUtil.toJSONString(request));
        LoginResponse loginResponse=userService.login(request);
        if (loginResponse==null || loginResponse.getUser()==null){
            log.info("[login] response:{}", JacksonUtil.toJSONString(request));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        log.info("[login] request:{}", JacksonUtil.toJSONString(loginResponse));
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "token", required = false) String token, HttpSession session) {
        session.removeAttribute(token);
        userService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(@RequestHeader(value = "token", required = false) String token) {
        List<User> friends=userService.getFriends(token);
        if (friends==null || friends.size()==0){
            return  new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(friends,HttpStatus.OK);
    }

    @GetMapping("/groups")
    public ResponseEntity<?> getGroups(@RequestHeader(value = "token", required = false) String token) {
        List<Group> groups=userService.geGroups(token);
        if (groups==null || groups.size()==0){
            return  new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(groups,HttpStatus.OK);
    }
}
