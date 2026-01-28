#!/bin/bash

echo "========================================"
echo "EZ-ADMIN 启动脚本"
echo "========================================"
echo ""

# 检查 JDK
if ! command -v java &> /dev/null; then
    echo "[错误] 未找到 Java，请安装 JDK 21+"
    exit 1
fi

echo "[信息] 检查 JDK 版本..."
java -version
echo ""

# JVM 参数
JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Spring 配置
SPRING_OPTS="--spring.profiles.active=dev"

echo "[信息] 正在启动 EZ-ADMIN..."
echo "[信息] 配置文件：application-dev.yml"
echo "[信息] 访问地址：http://localhost:8080"
echo "[信息] API 文档：http://localhost:8080/swagger-ui/index.html"
echo ""
echo "========================================"
echo ""

# 启动应用
java $JVM_OPTS -jar ez-admin-springboot4-1.0.0-beta.1.jar $SPRING_OPTS
