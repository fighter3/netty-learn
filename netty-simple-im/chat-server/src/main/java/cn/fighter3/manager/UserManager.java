package cn.fighter3.manager;

import cn.fighter3.model.User;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>Date: 2023/5/19 22:23</p>
 * <p>Author: fighter3</p>
 * <p>Description: 用户管理类
 * TODO:后续可以改成数据库+缓存的存储
 * </p>
 */
@Component
public class UserManager {
    /**
     * 所有用户：key:userId
     */
    private static final Map<String, User> USER_MAP=new ConcurrentHashMap<>();

    /**
     * 登录用户，key:token
     */
    private static final Map<String,User> LOGIN_USER_MAP=new ConcurrentHashMap<>();

    //直接静态初始化几个user
    static {
        User user1=new User("1","first","123","熊大","https://i.ibb.co/NW7CcTC/005-J4-OU5ly1h4cig1wumyj30o30o3dgm.jpg");
        User user2=new User("2","second","123","熊二","https://i.ibb.co/rdqzS99/006-APo-FYly8hek1sxo0fnj30hm0h2wf7.jpg");
        User user3=new User("3","fighter3","123","三某","https://i.ibb.co/xDPg0zx/0068-Lfdegy1h97eo0meppj30bo0bodge.jpg");
        User user4=new User("1024","four","123","李四","https://i.ibb.co/BNz2Mcm/0068-Lfdegy1h97eo4l047j30fc0eudgw.jpg");
        User user5=new User("521","five","123","王五","https://i.ibb.co/qr9hpQ8/b64da6adly1hejw7ztsqjj20pb0r5q7z.jpg");

        USER_MAP.put(user1.getUserId(), user1);
        USER_MAP.put(user2.getUserId(), user2);
        USER_MAP.put(user3.getUserId(), user3);
        USER_MAP.put(user4.getUserId(), user4);
        USER_MAP.put(user5.getUserId(), user5);
    }

    public  User getUserById(String userId){
        return USER_MAP.get(userId);
    }

    public  User getUserByUsername(String username){
        return USER_MAP.values()
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public void saveLoginUser(String uuid, User user) {
        LOGIN_USER_MAP.put(uuid,user);
    }

    public User getUserByToken(String token){
        if (token==null){
            return null;
        }
        return LOGIN_USER_MAP.get(token);
    }

    public void removeUserByToken(String token){
        LOGIN_USER_MAP.remove(token);
    }

    public List<User> getFriends(String userId) {
        List<User> otherUsers = USER_MAP.keySet()
                .stream()
                .filter(key -> !key.equals(userId))
                .map(USER_MAP::get)
                .collect(Collectors.toList());
        return otherUsers;
    }
}
