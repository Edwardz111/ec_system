package com.ec.service;

import com.ec.pojo.Address;
import com.ec.pojo.User;
import com.ec.pojo.dto.Logindto;
import com.ec.pojo.dto.Registerdto;
import com.ec.pojo.vo.Uservo;

import java.util.List;
import java.util.Map;

/**
 * 用户管理服务接口
 * ─────────────────────────────────────────────────────────────
 * 供其他模块调用的公开方法（接口约定）：
 *   • getUserById(Long)          —— 订单、评价模块获取用户信息
 *   • existsUser(Long)           —— 校验用户是否存在
 *   • getUsernameById(Long)      —— 管理员模块显示用户名
 * ─────────────────────────────────────────────────────────────
 */
public interface Userservice {

    // ── 注册 / 登录 ──────────────────────────────────────────

    /** 邮箱注册（含验证码校验） */
    void register(Registerdto dto);

    /** 用户名/邮箱 + 密码登录，返回 Session 信息 Map */
    Uservo loginByPassword(Logindto dto);



    /**
     * 邮箱验证码登录
     */
    Uservo loginByEmailCode(String email, String code);

    /** 发送邮箱验证码（type: LOGIN / REGISTER / RESET_PWD） */
    void sendEmailCode(String email, String type);

    // ── 微信登录 ─────────────────────────────────────────────

    /**
     * 生成微信扫码跳转 URL + state（返回给前端展示二维码）
     * @return Map{ "url": "...", "state": "..." }
     */
    Map<String, String> getWechatQrInfo();

    /**
     * 微信 OAuth 回调处理：用 code + state 换 openid，
     * 若已绑定则直接登录，否则返回 openid 等待绑定
     * @return Map{ "status":"login"|"bind", "userVO": UserVO|null, "openid": string|null }
     */
    Map<String, Object> handleWechatCallback(String code, String state);

    /** 将微信 openid 绑定到已有账号 */
    void bindWechat(Long userId, String openid);

    // ── 个人信息 ─────────────────────────────────────────────

    Uservo getUserById(Long id);

    /** 更新基本信息（昵称、年龄、性别等） */
    void updateInfo(User user);

    /** 修改密码（需校验旧密码） */
    void updatePassword(Long userId, String oldPwd, String newPwd);

    // ── 收货地址 ─────────────────────────────────────────────

    List<Address> listAddress(Long userId);

    void addAddress(Address address);

    void updateAddress(Address address);

    void deleteAddress(Long addressId, Long userId);

    void setDefaultAddress(Long addressId, Long userId);

    /** 供订单模块调用：获取用户默认地址 */
    Address getDefaultAddress(Long userId);

    /** 校验地址属于该用户后返回 */
    Address getAddressForUser(Long addressId, Long userId);

    // ── 对外暴露（其他模块调用） ──────────────────────────────

    /** 校验用户是否存在且可用（供订单/购物车模块调用） */
    boolean existsUser(Long userId);

    /** 获取用户名（供管理员模块、评价模块展示） */
    String getUsernameById(Long userId);

    /** 订单模块：按 ID 获取用户实体 */
    User findUserEntityById(Long id);

    /** 订单模块：获取全部用户 */
    List<User> getAllUsers();
}