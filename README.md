# 电子商务系统（整合版）

将 `demo`（用户）、`ec`（订单/购物车）、`product`（商品管理）三个模块合并为统一 Spring Boot 工程。

## 技术栈（架构已统一）

| 项目 | 说明 |
|------|------|
| Spring Boot | 2.7.18 |
| 持久层 | MyBatis（原 product 的 JPA 已转换） |
| 视图 | Thymeleaf + 静态页 |
| 数据库 | MySQL `ec_system` |
| 包名 | `com.ec` |

## 模块对应

| 原文件夹 | 整合后职责 |
|----------|------------|
| demo | 用户注册/登录/微信/地址、`Pagecontroller` |
| ec | 购物车、订单、`/product/list` 页面 |
| product | `/api/products`、`/api/categories`、`/api/admin` REST API |

## 启动

1. 创建数据库并执行脚本：
   - `src/main/resources/db.sql`（主表结构）
   - `src/main/resources/schema-extra.sql`（用户扩展表）
   - `src/main/resources/wbcsql.sql`（可选测试数据）
2. 配置数据库（二选一）：
   - 复制 `application-local.yaml.example` 为 `application-local.yaml`，填写本机 MySQL 密码
   - 或设置环境变量 `DB_PASSWORD=你的密码`
3. 默认连接 **product 模块库** `product_manage`（账号 root / 密码见 `application-local.yaml`）。订单、用户等表若缺失，需在库中执行 `db.sql`、`schema-extra.sql` 补全表结构
3. 运行：

```bash
cd system
mvn spring-boot:run
```

## 访问地址（与各原模块对应）

| 原模块 | 合并后地址 |
|--------|------------|
| **product** 商品浏览 | http://localhost:8080/ 或 `/index.html` |
| **product** 管理后台 | http://localhost:8080/admin.html |
| **demo** 登录/注册 | http://localhost:8080/user/login |
| **demo** 登录后首页 | http://localhost:8080/index |
| **ec** 商品列表+购物车 | http://localhost:8080/home/ec → `/product/list` |

## 冲突处理说明

- 根路径 `/` 由 `Pagecontroller` 跳转登录（已移除 ec `OrderController` 中的 `/`）
- ec 的 `UserController` 地址页合并到 `Pagecontroller`
- 用户服务统一为 `Userservice` / `Userserviceimpl`
- product 模块包名由 `com.example.product` 改为 `com.ec`
