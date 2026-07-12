-- 用户模块补充表（合并到 ec_system 数据库）
USE ec_system;

CREATE TABLE IF NOT EXISTS address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    receiver VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    detail VARCHAR(200) NOT NULL,
    postal_code VARCHAR(10) NULL,
    is_default TINYINT(1) DEFAULT 0 NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    INDEX idx_address_user (user_id)
) CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS email_verify_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL,
    expire_time DATETIME NOT NULL,
    used TINYINT(1) DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    INDEX idx_email_type (email, type)
) CHARSET=utf8mb4;

-- admin 表为「用户-管理员权限」映射，登录账号密码使用 users 表
-- 示例：user_id=1 且 permission_level 为 SUPER_ADMIN 或 ADMIN 的用户可登录后台
