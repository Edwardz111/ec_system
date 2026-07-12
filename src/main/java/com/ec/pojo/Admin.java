package com.ec.pojo;

import java.time.LocalDateTime;

/**
 * 管理员权限记录 —— 对应 admin 表（user_id + permission_level）
 * 登录账号密码在 users 表，本表仅标识该用户是否为管理员
 */
public class Admin {
    private Integer id;
    private Long userId;
    private String permissionLevel;  // SUPER_ADMIN / ADMIN / NORMAL
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 登录成功后从 users 表填充，便于页面展示 */
    private String username;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(String permissionLevel) { this.permissionLevel = permissionLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isManager() {
        return "SUPER_ADMIN".equals(permissionLevel) || "ADMIN".equals(permissionLevel);
    }
}
