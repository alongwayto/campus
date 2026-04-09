# 智能校园设备管理系统 — 部署指南

---

## 一、环境要求

| 组件 | 最低版本 | 推荐版本 | 说明 |
|------|---------|---------|------|
| JDK | 11 | 17 LTS | OpenJDK 或 Oracle JDK 均可 |
| Node.js | 16.x | 18 LTS | 包含 npm 8+ |
| MySQL | 8.0 | 8.0.33+ | 需开启 `utf8mb4` 字符集 |
| Maven | 3.6 | 3.8+ | 或使用项目内置 `mvnw` |
| Nginx | 1.20+ | 1.24+ | 生产环境反向代理（可选） |

---

## 二、开发环境搭建

### 2.1 克隆项目

```bash
git clone <repository-url>
cd campus-device-management
```

### 2.2 初始化数据库

```bash
# 确认 MySQL 服务已启动
mysql -u root -p -e "SELECT VERSION();"

# 导入初始化脚本
mysql -u root -p < sql/init.sql

# 验证
mysql -u root -p -e "USE campus_device; SHOW TABLES;"
```

### 2.3 配置后端

编辑 `backend/src/main/resources/application.yml`，修改数据源和 JWT 配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_device?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root          # 替换为实际数据库用户名
    password: your_password # 替换为实际数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: your_jwt_secret_key_at_least_32_chars  # 建议使用随机生成的强密钥
  expiration: 86400000  # Token 有效期，单位毫秒（默认 24 小时）
```

### 2.4 启动后端

```bash
cd backend

# 方式一：使用 Maven
mvn spring-boot:run

# 方式二：使用 Maven Wrapper（无需本地安装 Maven）
./mvnw spring-boot:run

# 验证后端启动
curl http://localhost:8080/actuator/health
```

后端默认监听端口 `8080`，可通过 `server.port` 配置修改。

### 2.5 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端默认监听端口 `3000`，开发代理配置位于 `vite.config.js`，已将 `/api` 请求代理至 `http://localhost:8080`。

---

## 三、生产环境部署

### 3.1 打包后端 JAR

```bash
cd backend

# 跳过测试进行打包
mvn clean package -DskipTests

# 生成的 JAR 位于
ls target/campus-device-*.jar
```

### 3.2 配置生产环境变量

生产部署时，**不要**将敏感信息写入配置文件，应通过环境变量注入：

```bash
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_NAME=campus_device
export DB_USERNAME=campus_user
export DB_PASSWORD=<强密码>
export JWT_SECRET=<随机生成的64位字符串>
```

`application-prod.yml` 中引用方式：

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

### 3.3 以后台服务运行 JAR

```bash
# 使用 nohup 后台运行
nohup java -Xms512m -Xmx1024m \
  -Dspring.profiles.active=prod \
  -jar target/campus-device-1.0.0.jar \
  > /var/log/campus-device/app.log 2>&1 &

# 查看进程
ps aux | grep campus-device

# 查看日志
tail -f /var/log/campus-device/app.log
```

> 推荐使用 **systemd** 管理服务进程，以实现开机自启和自动重启。

#### systemd 服务文件示例

创建 `/etc/systemd/system/campus-device.service`：

```ini
[Unit]
Description=Smart Campus Device Management System
After=network.target mysql.service

[Service]
User=campus
WorkingDirectory=/opt/campus-device
ExecStart=/usr/bin/java -Xms512m -Xmx1024m \
  -Dspring.profiles.active=prod \
  -jar /opt/campus-device/campus-device-1.0.0.jar
EnvironmentFile=/opt/campus-device/.env
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# 启用并启动服务
systemctl daemon-reload
systemctl enable campus-device
systemctl start campus-device
systemctl status campus-device
```

### 3.4 构建前端静态文件

```bash
cd frontend

# 生产构建
npm run build

# 构建产物位于 dist/ 目录
ls dist/
```

### 3.5 配置 Nginx

安装 Nginx 并创建站点配置 `/etc/nginx/conf.d/campus-device.conf`：

```nginx
server {
    listen       80;
    server_name  your-domain.com;  # 替换为实际域名或服务器 IP

    # 前端静态文件
    root  /opt/campus-device/frontend/dist;
    index index.html;

    # 前端 History 路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 反向代理后端 API
    location /api/ {
        proxy_pass         http://127.0.0.1:8080;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }

    # Gzip 压缩
    gzip            on;
    gzip_types      text/plain text/css application/json application/javascript;
    gzip_min_length 1024;
}
```

```bash
# 检查配置语法
nginx -t

# 重载配置
systemctl reload nginx
```

---

## 四、数据库运维

### 4.1 备份数据库

```bash
# 完整备份（推荐每日执行）
mysqldump -u root -p \
  --single-transaction \
  --routines \
  --triggers \
  campus_device > backup_$(date +%Y%m%d_%H%M%S).sql

# 压缩备份
mysqldump -u root -p --single-transaction campus_device | \
  gzip > backup_$(date +%Y%m%d).sql.gz
```

### 4.2 恢复数据库

```bash
# 从 .sql 文件恢复
mysql -u root -p campus_device < backup_20240101_120000.sql

# 从压缩文件恢复
gunzip -c backup_20240101.sql.gz | mysql -u root -p campus_device
```

### 4.3 创建专用数据库用户（生产推荐）

```sql
-- 仅授予应用所需权限，避免使用 root
CREATE USER 'campus_user'@'127.0.0.1' IDENTIFIED BY '<强密码>';
GRANT SELECT, INSERT, UPDATE, DELETE ON campus_device.* TO 'campus_user'@'127.0.0.1';
FLUSH PRIVILEGES;
```

---

## 五、常见问题

| 问题 | 可能原因 | 解决方案 |
|------|---------|---------|
| 后端启动失败，提示连接数据库拒绝 | 数据库未启动或配置错误 | 检查 MySQL 服务状态及 `application.yml` 配置 |
| 前端请求返回 401 | JWT Token 过期或未携带 | 重新登录获取新 Token |
| 前端构建失败 | Node.js 版本不兼容 | 升级至 Node.js 16+ |
| 中文乱码 | 数据库字符集配置错误 | 确认数据库和连接串均使用 `utf8mb4` |
| 端口被占用 | 其他进程占用 8080 或 3000 | 修改 `server.port` 或 `vite.config.js` 中的端口 |
