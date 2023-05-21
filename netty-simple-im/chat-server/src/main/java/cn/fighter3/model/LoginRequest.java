package cn.fighter3.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Date: 2023/5/16 1:33</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Data
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
