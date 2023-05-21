package cn.fighter3.service.impl;

import cn.fighter3.handle.WebSocketChannelHandler;
import cn.fighter3.manager.ChannelManager;
import cn.fighter3.manager.UserManager;
import cn.fighter3.model.LoginRequest;
import cn.fighter3.model.LoginResponse;
import cn.fighter3.model.User;
import cn.fighter3.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>Date: 2023/5/16 1:39</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WebSocketChannelHandler webSocketChannelHandler;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        User user= UserManager.getUserByUsername(request.getUsername());
        if (user==null || !Objects.equals(request.getPassword(),user.getPassword())){
            return response;
        }
        response.setUser(user);
        return response;
    }

}
