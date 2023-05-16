package cn.fighter3.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * <p>Date: 2023/5/16 1:36</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Data
public class ConfirmMessage implements Serializable {
    private String id;
    private String from;
    private String to;
    private String type;
}
