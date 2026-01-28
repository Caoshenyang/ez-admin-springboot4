@echo off
chcp 65001 >nul
echo ========================================
echo EZ-ADMIN 启动脚本
echo ========================================
echo.

REM 检查 JDK 版本
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java，请安装 JDK 21+
    pause
    exit /b 1
)

echo [信息] 检查 JDK 版本...
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%i
echo [信息] 当前 JDK 版本：%JAVA_VERSION%
echo.

REM 设置 JVM 参数
set JVM_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC

REM 设置配置文件
set SPRING_OPTS=--spring.profiles.active=dev

echo [信息] 正在启动 EZ-ADMIN...
echo [信息] 配置文件：application-dev.yml
echo [信息] 访问地址：http://localhost:8080
echo [信息] API 文档：http://localhost:8080/swagger-ui/index.html
echo.
echo ========================================
echo.

REM 启动应用
java %JVM_OPTS% -jar ez-admin-springboot4-1.0.0-beta.1.jar %SPRING_OPTS%

pause
