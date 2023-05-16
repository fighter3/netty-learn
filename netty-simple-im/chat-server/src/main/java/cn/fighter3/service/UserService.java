package cn.fighter3.service;

import cn.fighter3.model.LoginRequest;
import cn.fighter3.model.LoginResponse;
import cn.fighter3.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Date: 2023/5/16 1:39</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Service
public class UserService {
    private final List<User> userList = new ArrayList<>();

    public UserService() {
        // 在这里添加一些默认的用户信息
        User user1 = new User();
        user1.setId("1");
        user1.setUsername("张三");
        userList.add(user1);

        User user2 = new User();
        user2.setId("2");
        user2.setUsername("李四");
        userList.add(user2);

        User user3 = new User();
        user3.setId("3");
        user3.setUsername("王五");
        userList.add(user3);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        String username = request.getUsername();
        String password = request.getPassword();

        // 在这里根据用户名和密码验证用户身份
        // 如果验证成功，则返回登录响应，否则返回错误响应

        boolean success = true; // 假设验证成功
        if (success) {
            String userId = "1"; // 假设用户 ID 是 1
            User user = new User();
            user.setId(userId);
            user.setUsername(username);

            response.setCode(200);
            response.setMessage("登录成功");
            response.setUser(user);
            response.setToken("dummy-token"); // 这里可以生成一个随机的登录令牌
        } else {
            response.setCode(401);
            response.setMessage("用户名或密码错误");
        }

        return response;
    }

    /**
     * 获取用户列表
     */
    public List<User> getUserList() {
        return userList;
    }
}
