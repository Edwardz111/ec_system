-- ============================================================
-- ec_system 数据库初始化脚本（Cloud Agent 环境用）
--
-- 说明：仓库中 README 提到的主结构脚本 db.sql / wbcsql.sql 缺失，
-- 本文件根据 MyBatis Mapper（src/main/resources/mapper/*.xml）与
-- 注解 Mapper（com.ec.mapper.*）以及 POJO 字段整合出完整表结构，
-- 统一使用 application.yaml 默认的 ec_system 库。
--
-- 幂等：所有表使用 CREATE TABLE IF NOT EXISTS；种子数据使用
-- INSERT ... ON DUPLICATE KEY / 固定主键，可重复执行。
-- ============================================================

CREATE DATABASE IF NOT EXISTS ec_system
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ec_system;

-- ---------- 用户 ----------
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password      VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NULL,
    phone         VARCHAR(20)  NULL,
    age           INT          NULL,
    gender        VARCHAR(10)  NULL,
    role          VARCHAR(20)  DEFAULT 'USER',
    is_vip        TINYINT(1)   DEFAULT 0,
    status        VARCHAR(20)  DEFAULT 'ACTIVE',
    wechat_openid VARCHAR(64)  NULL,
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_users_username (username),
    KEY idx_users_email (email),
    KEY idx_users_wechat (wechat_openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

-- ---------- 管理员权限映射 ----------
CREATE TABLE IF NOT EXISTS admin (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    permission_level VARCHAR(20)  NOT NULL COMMENT 'SUPER_ADMIN / ADMIN / NORMAL',
    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admin_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员权限';

-- ---------- 商品分类 ----------
CREATE TABLE IF NOT EXISTS product_category (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    parent_id   BIGINT       NULL,
    KEY idx_category_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类';

-- ---------- 商品 ----------
CREATE TABLE IF NOT EXISTS products (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(200)   NOT NULL,
    description   TEXT           NULL,
    price         DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    image         VARCHAR(500)   NULL,
    stock         INT            NOT NULL DEFAULT 0,
    sales_volume  INT            NOT NULL DEFAULT 0,
    category_id   BIGINT         NULL,
    enabled       TINYINT(1)     DEFAULT 1,
    created_at    DATETIME       DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_products_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品';

-- ---------- 收货地址 ----------
CREATE TABLE IF NOT EXISTS address (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    receiver     VARCHAR(50)  NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    province     VARCHAR(50)  NULL,
    city         VARCHAR(50)  NULL,
    district     VARCHAR(50)  NULL,
    detail       VARCHAR(200) NOT NULL,
    postal_code  VARCHAR(20)  NULL,
    is_default   TINYINT(1)   DEFAULT 0,
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    KEY idx_address_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址';

-- ---------- 邮箱验证码 ----------
CREATE TABLE IF NOT EXISTS email_verify_code (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(100) NOT NULL,
    code        VARCHAR(10)  NOT NULL,
    type        VARCHAR(20)  NOT NULL COMMENT 'LOGIN/REGISTER/RESET_PWD',
    used        TINYINT(1)   DEFAULT 0,
    expire_time DATETIME     NOT NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    KEY idx_email_type (email, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码';

-- ---------- 购物车 ----------
CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT   NOT NULL,
    product_id BIGINT   NOT NULL,
    quantity   INT      NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_cart_user (user_id),
    KEY idx_cart_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

-- ---------- 订单 ----------
CREATE TABLE IF NOT EXISTS orders (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no        VARCHAR(64)   NULL,
    user_id         BIGINT        NOT NULL,
    address_id      BIGINT        NULL,
    total_amount    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20)   NULL,
    payment_method  VARCHAR(20)   NULL,
    shipping_status VARCHAR(20)   NULL,
    coupon_id       BIGINT        NULL,
    discount_amount DECIMAL(10,2) NULL DEFAULT 0.00,
    remark          VARCHAR(255)  NULL,
    item_count      INT           NULL,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_orders_user (user_id),
    KEY idx_orders_address (address_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

-- ---------- 订单明细 ----------
CREATE TABLE IF NOT EXISTS order_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT        NOT NULL,
    product_id BIGINT        NOT NULL,
    quantity   INT           NOT NULL DEFAULT 1,
    price      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME      DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_items_order (order_id),
    KEY idx_order_items_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

-- ---------- 商品评价 ----------
CREATE TABLE IF NOT EXISTS product_reviews (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    user_id    BIGINT       NULL,
    order_id   BIGINT       NULL,
    rating     INT          NULL,
    content    TEXT         NULL,
    images     TEXT         NULL,
    reply      TEXT         NULL,
    status     VARCHAR(20)  DEFAULT 'VISIBLE',
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_reviews_product (product_id),
    KEY idx_reviews_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价';

-- ---------- 促销 ----------
CREATE TABLE IF NOT EXISTS promotions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(30)  NULL,
    description VARCHAR(255) NULL,
    rule        VARCHAR(500) NULL,
    enabled     TINYINT(1)   DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销活动';

CREATE TABLE IF NOT EXISTS promotion_products (
    promotion_id BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    PRIMARY KEY (promotion_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销-商品关联';

-- ============================================================
-- 种子数据（幂等：固定主键 + INSERT IGNORE / ON DUPLICATE KEY）
-- ============================================================

-- 分类
INSERT INTO product_category (category_id, name, parent_id) VALUES
    (1, '手机数码', NULL),
    (2, '服装', NULL),
    (3, '家用电器', NULL)
ON DUPLICATE KEY UPDATE name = VALUES(name), parent_id = VALUES(parent_id);

-- 商品
INSERT INTO products (id, name, description, price, image, stock, sales_volume, category_id, enabled) VALUES
    (1, '小米手机 14', '小米旗舰智能手机，骁龙处理器', 3999.00, NULL, 100, 320, 1, 1),
    (2, '华为 Mate 60', '华为高端旗舰，麒麟芯片', 5999.00, NULL, 80, 210, 1, 1),
    (3, '连衣裙 夏季新款', '轻薄透气，多色可选', 199.00, NULL, 500, 150, 2, 1),
    (4, '海尔冰箱 三门', '风冷无霜，节能静音', 2599.00, NULL, 40, 55, 3, 1),
    (5, '苹果 iPhone 15', 'Apple A16 仿生芯片', 6499.00, NULL, 60, 500, 1, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), price = VALUES(price),
    stock = VALUES(stock), sales_volume = VALUES(sales_volume),
    category_id = VALUES(category_id), enabled = VALUES(enabled);

-- 用户（密码明文 123456，与 demo 模块一致）；user_id=1 为管理员
INSERT INTO users (id, username, password, email, phone, age, gender, role, is_vip, status) VALUES
    (1, 'admin',  '123456', 'admin@test.com',  '13800000001', 30, '男', 'ADMIN', 1, 'ACTIVE'),
    (2, 'alice',  '123456', 'alice@test.com',  '13800000002', 24, '女', 'USER',  0, 'ACTIVE'),
    (3, 'bob',    '123456', 'bob@test.com',    '13800000003', 28, '男', 'USER',  1, 'ACTIVE')
ON DUPLICATE KEY UPDATE username = VALUES(username), password = VALUES(password),
    email = VALUES(email), role = VALUES(role), status = VALUES(status);

-- 管理员权限
INSERT INTO admin (id, user_id, permission_level) VALUES
    (1, 1, 'SUPER_ADMIN')
ON DUPLICATE KEY UPDATE permission_level = VALUES(permission_level);

-- 收货地址（供订单 address_id 使用）
INSERT INTO address (id, user_id, receiver, phone, province, city, district, detail, postal_code, is_default) VALUES
    (1, 2, 'Alice', '13800000002', '广东省', '深圳市', '南山区', '科技园 1 号', '518000', 1)
ON DUPLICATE KEY UPDATE receiver = VALUES(receiver), detail = VALUES(detail);

-- 一条示例订单 + 明细
INSERT INTO orders (id, order_no, user_id, address_id, total_amount, status, payment_method, shipping_status, discount_amount, item_count) VALUES
    (1, 'ORD20240101001', 2, 1, 3999.00, 'PAID', 'ALIPAY', 'PENDING_SHIP', 0.00, 1)
ON DUPLICATE KEY UPDATE total_amount = VALUES(total_amount), status = VALUES(status);

INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
    (1, 1, 1, 1, 3999.00)
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), price = VALUES(price);

-- 一条示例评价
INSERT INTO product_reviews (id, product_id, user_id, order_id, rating, content, status) VALUES
    (1, 1, 2, 1, 5, '手机很好用，物流很快！', 'VISIBLE')
ON DUPLICATE KEY UPDATE rating = VALUES(rating), content = VALUES(content), status = VALUES(status);
