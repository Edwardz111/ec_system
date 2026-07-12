-- 清空 product_manage 库中所有表数据（保留表结构）
-- ⚠️ 不可恢复，执行前请确认已备份

USE product_manage;

SET FOREIGN_KEY_CHECKS = 0;

-- 关联表 / 子表（先清）
TRUNCATE TABLE promotion_products;
TRUNCATE TABLE order_items;
TRUNCATE TABLE product_reviews;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE user_coupons;
TRUNCATE TABLE orders;
TRUNCATE TABLE address;
TRUNCATE TABLE email_verify_code;

-- 主业务表
TRUNCATE TABLE products;
TRUNCATE TABLE promotions;
TRUNCATE TABLE product_category;
TRUNCATE TABLE users;
TRUNCATE TABLE coupons;

SET FOREIGN_KEY_CHECKS = 1;

-- 若某表不存在会报错，可注释掉对应行后重新执行
