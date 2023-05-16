package cn.fighter3.model;

import lombok.Data;

/**
 * <p>Date: 2023/5/16 1:34</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Data
public class LoginResponse {
    private static final long serialVersionUID = -1L;

    private int code;
    private String message;
    private User user;
    private String token;
}
