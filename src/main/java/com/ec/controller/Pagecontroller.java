package com.ec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由 Controller（整合版）
 *
 * 整合了：
 *  - 用户模块的登录/注册/首页
 *  - 根路径：与 product 原工程一致，进入商品浏览首页 /index.html
 *  - demo 登录后首页仍为 /index（Thymeleaf）
 *
 * ⚠️ 必须用 @Controller，不能用 @RestController
 *    注意：ec 模块的 OrderController 里有 @GetMapping("/")，
 *    已将其删除，统一由本类处理根路径
 */
@Controller
public class Pagecontroller {

    /** 根路径 → 商品浏览（原 product 模块默认页） */
    @GetMapping("/")
    public String root() {
        return "redirect:/index.html";
    }

    /** ec 模块原入口：商品列表 + 购物车 */
    @GetMapping("/home/ec")
    public String ecHome() {
        return "redirect:/product/list";
    }

    /** GET /user/login → templates/user/Login.html */
    @GetMapping("/user/login")
    public String loginPage() {
        return "user/Login";
    }

    /** GET /user/register → templates/user/Register.html */
    @GetMapping("/user/register")
    public String registerPage() {
        return "user/Register";
    }

    /** GET /index → templates/index.html（登录成功后首页） */
    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    /** GET /user/address → 收货地址管理页 */
    @GetMapping("/user/address")
    public String addressPage() {
        return "user/address";
    }

    /** GET /user/profile → 个人信息页 */
    @GetMapping("/user/profile")
    public String profilePage() {
        return "user/profile";
    }

    /** GET /admin → 管理后台入口，跳登录页 */
    @GetMapping("/admin")
    public String adminEntry() {
        return "redirect:/admin/login";
    }
}