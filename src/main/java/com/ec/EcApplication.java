package com.ec;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 电子商务系统启动类
 *
 * 整合说明：
 *  - 用户模块（王梓骏）：com.ec.controller.PageController / UserController
 *                         com.ec.service.impl.Userserviceimpl
 *  - 订单/购物车模块（王柏程）：com.ec.controller.OrderController
 *  - 商品模块（杨宇豪）：com.ec.controller.ProductApiController / AdminProductController
 *
 * 数据库：统一使用 ec_system（见 application.yml）
 */
@SpringBootApplication
@MapperScan("com.ec.mapper")
public class EcApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcApplication.class, args);
        System.out.println("========================================");
        System.out.println("  电子商务系统启动成功！");
        System.out.println("  商品前台(默认): http://localhost:8080/");
        System.out.println("  管理后台:       http://localhost:8080/admin/login");
        System.out.println("  商品管理:       http://localhost:8080/product_admin.html");
        System.out.println("  用户登录:       http://localhost:8080/user/login");
        System.out.println("  ec购物  车/订单:  http: //localhost:8080/home/ec");
        System.out.println("========================================");
    }
}