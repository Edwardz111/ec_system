-- 订单表增加收货地址外键（若已存在该列可忽略报错）
USE ec_system;

ALTER TABLE orders
    ADD COLUMN address_id BIGINT NULL COMMENT '收货地址ID' AFTER user_id;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_address
        FOREIGN KEY (address_id) REFERENCES address (id)
            ON UPDATE CASCADE ON DELETE SET NULL;

-- 将已有订单的收货地址统一设为 1（请确保 address 表中存在 id=1 的记录）
UPDATE orders SET address_id = 1;
