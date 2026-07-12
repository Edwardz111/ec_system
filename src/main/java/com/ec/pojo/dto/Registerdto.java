package com.ec.pojo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

// ══════════════════════════════════════════════════
//  用户注册 DTO
// ══════════════════════════════════════════════════
public class Registerdto {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度 3-20 位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度 6-20 位")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 邮箱验证码 */
    @NotBlank(message = "验证码不能为空")
    private String code;

    private String phone;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}