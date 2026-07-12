package com.ec.pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体 —— 对应 users 表
 * 字段兼容 ec模块（createdAt/updatedAt）和 用户模块（wechatOpenid/isVip/role）
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long          id;
    private String        username;
    private String        password;
    private String        email;
    private String        phone;
    private Integer       age;
    private String        gender;
    private String        role;          // USER / ADMIN
    private Integer       isVip;         // 0-普通 1-VIP
    private String        status;        // ACTIVE / DISABLED
    private String        wechatOpenid;  // 微信登录绑定
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}