-- 在 product_manage 库中补全 demo 用户模块所需表（与 AddressMapper / EmailverifycodeMapper 字段一致）
USE product_manage;

-- 微信登录字段（若已存在会报错，可忽略）
-- ALTER TABLE users ADD COLUMN wechat_openid VARCHAR(64) NULL COMMENT '微信OpenID';

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
    INDEX idx_address_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址';

CREATE TABLE IF NOT EXISTS email_verify_code (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(100) NOT NULL,
    code        VARCHAR(10)  NOT NULL,
    type        VARCHAR(20)  NOT NULL COMMENT 'LOGIN/REGISTER/RESET_PWD',
    used        TINYINT(1)   DEFAULT 0,
    expire_time DATETIME     NOT NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email_type (email, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码';

-- ec 购物车（商品加购需要）
CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT      DEFAULT 1 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    INDEX idx_cart_items_user_id (user_id),
    INDEX idx_cart_items_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';
