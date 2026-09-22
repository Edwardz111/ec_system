#!/usr/bin/env bash
# 幂等安装脚本：编译打包 Spring Boot 应用。
# 系统依赖（JDK17 / Maven / MySQL）来自环境快照（base image），此处不再安装。
set -euo pipefail

cd "$(dirname "$0")/.."

# 使用 JDK 17（快照中已通过 update-alternatives 设为默认，这里再兜底导出）
if [ -d /usr/lib/jvm/java-17-openjdk-amd64 ]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
fi

# 依赖已缓存在 ~/.m2（快照内），离线增量构建；跳过测试（测试需运行中的 MySQL）
mvn -B -DskipTests clean package

echo "[install] build finished: $(ls -1 target/*.jar 2>/dev/null || echo 'NO JAR')"
