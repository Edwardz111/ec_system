#!/usr/bin/env bash
# 每次启动时运行：拉起 MySQL 守护进程并确保 ec_system 库/表存在（幂等）。
# 不做依赖安装或编译（那些属于 install 阶段）。
set -euo pipefail

cd "$(dirname "$0")/.."

DB_PASS="${DB_PASSWORD:-zjd056088}"
# 应用通过 TCP(JDBC) 连接；这里的 CLI 同样强制走 TCP，避免 socket 权限问题
MYSQL_TCP=(mysql --protocol=TCP -h 127.0.0.1 -P 3306 -uroot -p"${DB_PASS}" --get-server-public-key)

echo "[start] starting mysql ..."
sudo service mysql start || true

# 等待 MySQL 就绪（mysqld 响应即可）
for i in $(seq 1 30); do
  if sudo mysqladmin ping >/dev/null 2>&1; then
    echo "[start] mysqld is alive"
    break
  fi
  sleep 1
done

# 兜底确保应用账号可通过密码登录（快照中一般已配置好，全新数据目录则用 socket 初始化）
CRED_SQL="$(mktemp)"
cat > "$CRED_SQL" <<SQL
ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY '${DB_PASS}';
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED WITH caching_sha2_password BY '${DB_PASS}';
ALTER USER 'root'@'%' IDENTIFIED WITH caching_sha2_password BY '${DB_PASS}';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
SQL
"${MYSQL_TCP[@]}" < "$CRED_SQL" 2>/dev/null \
  || sudo mysql < "$CRED_SQL" 2>/dev/null \
  || echo "[start] warn: could not (re)apply credentials, continuing"
rm -f "$CRED_SQL"

# 确保库与表结构、种子数据存在（CREATE TABLE IF NOT EXISTS / ON DUPLICATE KEY，幂等）
echo "[start] applying schema (idempotent) ..."
"${MYSQL_TCP[@]}" < .cursor/db-init.sql 2>/dev/null \
  || sudo mysql < .cursor/db-init.sql

echo "[start] ec_system ready:"
"${MYSQL_TCP[@]}" -N -e \
  "USE ec_system; SELECT CONCAT('  products=', (SELECT COUNT(*) FROM products), ' users=', (SELECT COUNT(*) FROM users));" 2>/dev/null
