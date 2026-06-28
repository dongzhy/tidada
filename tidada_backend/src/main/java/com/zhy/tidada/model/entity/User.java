package com.zhy.tidada.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * 用户实体
 *
 * @author <a href="https://github.com/dongzhy">程序员zhy</a>
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user")
@Data
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码（加密存储）
     */
    private String userPassword;

    /**
     * 开放平台unionId
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像地址
     */
    private String userAvatar;

    /**
     * 用户个人简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 非数据库字段：序列化标识
     */
    @TableField(exist = false)
    private static final long serialVersionUid = 1L;
}