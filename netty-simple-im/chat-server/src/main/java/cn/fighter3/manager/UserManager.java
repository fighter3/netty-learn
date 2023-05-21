package cn.fighter3.manager;

import cn.fighter3.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Date: 2023/5/19 22:23</p>
 * <p>Author: fighter3</p>
 * <p>Description: 用户管理类</p>
 */
public class UserManager {
    /**
     * 用户map，key:userId
     */
    private static final Map<String, User> USER_MAP=new ConcurrentHashMap<>();

    //直接静态初始化几个user
    static {
        User user1=new User("1","first","123","熊大");
        User user2=new User("2","second","123","熊二");
        User user3=new User("3","fighter3","123","三某");
        User user4=new User("1024","four","123","李四");
        User user5=new User("521","five","123","王五");

        USER_MAP.put(user1.getUserId(), user1);
        USER_MAP.put(user2.getUserId(), user2);
        USER_MAP.put(user3.getUserId(), user3);
        USER_MAP.put(user4.getUserId(), user4);
        USER_MAP.put(user5.getUserId(), user5);
    }

    public static User getUserById(String userId){
        return USER_MAP.get(userId);
    }

    public static User getUserByUsername(String username){
        return USER_MAP.values()
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
