package cn.fighter3.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2023/5/16 1:34</p>
 * <p>Author: fighter3</p>
 * <p>Description: 登录响应类</p>
 */
@Data
public class LoginResponse implements Serializable {
    /**
     * 用户
     */
    private User user;
    /**
     * token
     */
    private String token;
}
