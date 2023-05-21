package cn.fighter3.service;

import cn.fighter3.model.LoginRequest;
import cn.fighter3.model.LoginResponse;
import cn.fighter3.model.User;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Date: 2023/5/16 1:39</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
public interface UserService {
    /**
     * 用户登录
     */
     LoginResponse login(LoginRequest request) ;

}
