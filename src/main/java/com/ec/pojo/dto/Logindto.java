package com.ec.pojo.dto;

import javax.validation.constraints.NotBlank;

// ══════════════════════════════════════════════════
//  用户名/密码登录 DTO
// ══════════════════════════════════════════════════
public class Logindto {

    @NotBlank(message = "账号不能为空")
    private String account;    // 用户名 或 邮箱

    @NotBlank(message = "密码不能为空")
    private String password;

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}