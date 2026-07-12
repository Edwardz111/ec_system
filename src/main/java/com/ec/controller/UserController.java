package com.ec.controller;

import com.ec.pojo.Address;
import com.ec.pojo.User;
import com.ec.pojo.dto.Logindto;
import com.ec.pojo.dto.Registerdto;
import com.ec.pojo.vo.Result;
import com.ec.pojo.vo.Uservo;
import com.ec.service.Userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private Userservice userService;

    // ══════════════════════════════════════════════════════
    //  验证码 & 注册
    // ══════════════════════════════════════════════════════

    /**
     * POST /user/sendCode?email=xxx&type=LOGIN
     * 发送邮箱验证码
     * 前端必须用 POST 调用，不能用 GET（GET 会被 Spring 拦截报 405）
     */
    @PostMapping("/sendCode")
    public Result<Void> sendCode(@RequestParam String email,
                                 @RequestParam(defaultValue = "LOGIN") String type) {
        userService.sendEmailCode(email, type);
        return Result.success();
    }

    /**
     * POST /user/register
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated Registerdto dto) {
        userService.register(dto);
        return Result.success();
    }

    // ══════════════════════════════════════════════════════
    //  登录 / 登出
    // ══════════════════════════════════════════════════════

    /** POST /user/login  用户名/邮箱 + 密码 */
    @PostMapping("/login")
    public Result<Uservo> login(@RequestBody @Validated Logindto dto, HttpSession session) {
        Uservo userVO = userService.loginByPassword(dto);
        session.setAttribute("USER_INFO", userVO);
        return Result.success(userVO);
    }

    /**
     * POST /user/loginByCode
     * 邮箱验证码登录（前端用 POST + query params）
     */
    @PostMapping("/loginByCode")
    public Result<Uservo> loginByCode(@RequestParam String email,
                                      @RequestParam String code,
                                      HttpSession session) {
        Uservo userVO = userService.loginByEmailCode(email, code);
        session.setAttribute("USER_INFO", userVO);
        return Result.success(userVO);
    }

    /** GET /user/logout */
    @GetMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    // ══════════════════════════════════════════════════════
    //  微信扫码登录
    // ══════════════════════════════════════════════════════

    /** GET /user/wechat/qr  获取微信授权 URL 和 state */
    @GetMapping("/wechat/qr")
    public Result<Map<String, String>> getWechatQr() {
        return Result.success(userService.getWechatQrInfo());
    }

    /**
     * GET /user/wechat/callback  微信 OAuth 回调
     * 微信服务器用 GET 重定向到此地址
     */
    @GetMapping("/wechat/callback")
    public Result<Map<String, Object>> wechatCallback(@RequestParam String code,
                                                      @RequestParam String state,
                                                      HttpSession session) {
        Map<String, Object> result = userService.handleWechatCallback(code, state);
        if ("login".equals(result.get("status"))) {
            Uservo userVO = (Uservo) result.get("userVO");
            session.setAttribute("USER_INFO", userVO);
        }
        return Result.success(result);
    }

    /** POST /user/wechat/bind */
    @PostMapping("/wechat/bind")
    public Result<Void> bindWechat(@RequestParam String openid, HttpSession session) {
        Uservo userVO = getCurrentUser(session);
        userService.bindWechat(userVO.getId(), openid);
        return Result.success();
    }

    // ══════════════════════════════════════════════════════
    //  个人信息
    // ══════════════════════════════════════════════════════

    /** GET /user/info */
    @GetMapping("/info")
    public Result<Uservo> getInfo(HttpSession session) {
        Uservo userVO = (Uservo) session.getAttribute("USER_INFO");
        if (userVO == null) return Result.unauthorized();   // 未登录返回 401，不抛异常
        return Result.success(userService.getUserById(userVO.getId()));
    }

    /** PUT /user/update */
    @PutMapping("/update")
    public Result<Void> updateInfo(@RequestBody User user, HttpSession session) {
        Uservo cur = getCurrentUser(session);
        user.setId(cur.getId());
        userService.updateInfo(user);
        return Result.success();
    }

    /** PUT /user/updatePwd */
    @PutMapping("/updatePwd")
    public Result<Void> updatePassword(@RequestParam String oldPwd,
                                       @RequestParam String newPwd,
                                       HttpSession session) {
        Uservo cur = getCurrentUser(session);
        userService.updatePassword(cur.getId(), oldPwd, newPwd);
        return Result.success();
    }

    // ══════════════════════════════════════════════════════
    //  收货地址
    // ══════════════════════════════════════════════════════

    @GetMapping("/address/list")
    public Result<List<Address>> listAddress(HttpSession session) {
        Uservo cur = getCurrentUser(session);
        return Result.success(userService.listAddress(cur.getId()));
    }

    @PostMapping("/address/add")
    public Result<Void> addAddress(@RequestBody Address address, HttpSession session) {
        Uservo cur = getCurrentUser(session);
        address.setUserId(cur.getId());
        userService.addAddress(address);
        return Result.success();
    }

    @PutMapping("/address/update")
    public Result<Void> updateAddress(@RequestBody Address address, HttpSession session) {
        getCurrentUser(session);
        userService.updateAddress(address);
        return Result.success();
    }

    @DeleteMapping("/address/delete")
    public Result<Void> deleteAddress(@RequestParam Long id, HttpSession session) {
        Uservo cur = getCurrentUser(session);
        userService.deleteAddress(id, cur.getId());
        return Result.success();
    }

    @PutMapping("/address/setDefault")
    public Result<Void> setDefault(@RequestParam Long id, HttpSession session) {
        Uservo cur = getCurrentUser(session);
        userService.setDefaultAddress(id, cur.getId());
        return Result.success();
    }

    // ══════════════════════════════════════════════════════
    //  工具
    // ══════════════════════════════════════════════════════

    private Uservo getCurrentUser(HttpSession session) {
        Uservo userVO = (Uservo) session.getAttribute("USER_INFO");
        if (userVO == null) throw new RuntimeException("未登录");
        return userVO;
    }
}