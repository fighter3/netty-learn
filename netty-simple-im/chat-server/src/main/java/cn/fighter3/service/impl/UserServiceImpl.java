package cn.fighter3.service.impl;

import cn.fighter3.handle.WebSocketChannelHandler;
import cn.fighter3.manager.ChannelManager;
import cn.fighter3.manager.UserManager;
import cn.fighter3.model.Group;
import cn.fighter3.model.LoginRequest;
import cn.fighter3.model.LoginResponse;
import cn.fighter3.model.User;
import cn.fighter3.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>Date: 2023/5/16 1:39</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WebSocketChannelHandler webSocketChannelHandler;

    @Resource
    private UserManager userManager;
    @Resource
    private ChannelManager channelManager;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        User user= userManager.getUserByUsername(request.getUsername());
        if (user==null || !Objects.equals(request.getPassword(),user.getPassword())){
            return response;
        }
        response.setUser(user);
        //直接拿http_session存储user
        String uuid= UUID.randomUUID().toString();
        userManager.saveLoginUser(uuid,user);
        response.setToken(uuid);
        return response;
    }

    @Override
    public void logout(String token) {
        userManager.removeUserByToken(token);
    }

    @Override
    public List<User> getFriends(String token) {
        User user=userManager.getUserByToken(token);
        if (user==null){
            return null;
        }
        //获取在线好友
        List<User> friends=userManager.getFriends(user.getUserId());
        return friends;
    }

    @Override
    public List<Group> geGroups(String token) {
        //直接写死一个群组
        Group group=Group.builder()
                .groupId("1")
                .name("人才交流群")
                .avatar("https://i.ibb.co/LtbkY6H/ceeb653ely8hegwd0q62fj20hs0g8wf5.jpg")
                .build();
        List<Group> groups=new ArrayList<>();
        groups.add(group);
        return groups;
    }

}
