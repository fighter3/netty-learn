package cn.fighter3.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2023/5/16 1:34</p>
 * <p>Author: fighter3</p>
 * <p>Description: 用户实体类</p>
 */
@Data
@AllArgsConstructor
public class User implements Serializable {

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
}
