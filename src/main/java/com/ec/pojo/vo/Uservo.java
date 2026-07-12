package com.ec.pojo.vo;

import java.time.LocalDateTime;

/**
 * 用户视图对象 —— 返回给前端时隐藏密码等敏感字段
 */
public class Uservo {

    private Long   id;
    private String username;
    private String email;
    private String phone;
    private Integer age;
    private String  gender;
    private String  avatar;
    private Integer vipLevel;
    /** 前端约定：1-正常 0-禁用 */
    private Integer status;
    private LocalDateTime createTime;

    // ── 来自 User 实体的构建方法（桥接 ec 模块字段 → 用户模块 VO）──

    public static Uservo of(com.ec.pojo.User u) {
        Uservo vo = new Uservo();
        vo.id         = u.getId();
        vo.username   = u.getUsername();
        vo.email      = u.getEmail();
        vo.phone      = u.getPhone();
        vo.age        = u.getAge();
        vo.gender     = u.getGender();
        vo.avatar     = null;
        vo.vipLevel   = u.getIsVip() != null ? u.getIsVip() : 0;
        vo.status     = toStatusCode(u.getStatus());
        vo.createTime = u.getCreatedAt();
        return vo;
    }

    /** ACTIVE / 1 → 1；DISABLED / 0 → 0 */
    private static int toStatusCode(String status) {
        if (status == null) return 1;
        if ("DISABLED".equalsIgnoreCase(status) || "BANNED".equalsIgnoreCase(status) || "0".equals(status)) return 0;
        return 1;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Integer getAge() { return age; }
    public String getGender() { return gender; }
    public String getAvatar() { return avatar; }
    public Integer getVipLevel() { return vipLevel; }
    public Integer getStatus() { return status; }
    public LocalDateTime getCreateTime() { return createTime; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String u) { this.username = u; }
    public void setEmail(String e) { this.email = e; }
    public void setPhone(String p) { this.phone = p; }
    public void setAge(Integer a) { this.age = a; }
    public void setGender(String g) { this.gender = g; }
    public void setAvatar(String av) { this.avatar = av; }
    public void setVipLevel(Integer v) { this.vipLevel = v; }
    public void setStatus(Integer s) { this.status = s; }
    public void setCreateTime(LocalDateTime t) { this.createTime = t; }
}