package cn.fighter3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>Date: 2023/6/4 22:19</p>
 * <p>Author: fighter3</p>
 * <p>Description: 群组</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group implements Serializable {
    /**
     * 群组id
     */
    private String groupId;
    /**
     * 群组名称
     */
    private String name;
    /**
     * 头像
     */
    private String avatar;
}
