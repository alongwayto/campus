# 安装部署指南

## 环境要求

| 组件 | 版本要求 |
|------|---------|
| JDK | 11+ |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 6.0+（可选，用于缓存） |
| Node.js | 16+ |
| npm | 7+ |

## 快速启动

### 1. 克隆项目

```bash
git clone https://github.com/alongwayto/campus.git
cd campus/campus-device-management
```

### 2. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

### 3. 配置后端

编辑 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_device?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问：
- 应用: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端访问：http://localhost:5173

## Docker 部署

```bash
# 使用 Docker Compose 一键部署
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

## 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |
| maintainer | maint123 | 维护员 |
| user1 | user123 | 普通用户 |

## 常见问题

**Q: 启动时提示数据库连接失败**  
A: 检查 MySQL 是否启动，用户名密码是否正确，数据库 campus_device 是否存在。

**Q: Redis 连接失败**  
A: Redis 为可选组件。若不使用 Redis，可在 application.yml 中注释掉 spring.redis 配置。

**Q: 前端页面空白**  
A: 检查 vite.config.js 中的 proxy 配置是否指向正确的后端地址。
