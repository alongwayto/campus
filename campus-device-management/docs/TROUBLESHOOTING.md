# 故障排查指南

## 后端常见问题

### 1. 启动报错：数据库连接失败
```
Failed to obtain JDBC Connection: Access denied for user 'root'@'localhost'
```
**解决方案**：
- 确认 MySQL 已启动：`systemctl status mysql`
- 检查用户名密码是否正确
- 确认数据库已创建：`mysql -u root -p -e "show databases;"`

### 2. JWT Token 解析失败
```
io.jsonwebtoken.security.SignatureException: JWT signature does not match
```
**解决方案**：
- 检查 `jwt.secret` 配置是否一致
- Token 长度是否过短（建议 32 字符以上）

### 3. Redis 连接失败（非必需）
```
Cannot get Jedis connection; connection refused
```
**解决方案**：
- 若不需要 Redis 缓存，注释掉 application.yml 中 `spring.redis` 配置
- 若需要 Redis，启动 Redis：`redis-server`

### 4. WebSocket 握手失败
```
WebSocket connection failed: Error during WebSocket handshake
```
**解决方案**：
- 检查 CORS 配置，确保前端域名在允许列表中
- 检查 Nginx 配置是否支持 WebSocket 升级

### 5. Swagger UI 无法访问
访问 `http://localhost:8081/swagger-ui.html` 时 404

**解决方案**：
- 确认 `springdoc-openapi-ui` 依赖已添加（非 springfox）
- 检查 SecurityConfig 是否放行 `/swagger-ui/**` 和 `/v3/api-docs/**`

## 前端常见问题

### 1. 跨域错误（CORS）
```
Access to XMLHttpRequest at 'http://localhost:8081/api/...' from origin 'http://localhost:5173' has been blocked by CORS policy
```
**解决方案**：
- 确认后端 `CorsConfig.java` 允许前端域名
- 检查 `vite.config.js` 的代理配置

### 2. 登录后页面空白
**解决方案**：
- 检查 Token 是否正确存储到 localStorage
- 打开浏览器开发者工具 → Network，查看 API 请求状态
- 检查 router/index.js 路由守卫配置

### 3. ECharts 图表不显示
**解决方案**：
- 确认容器 div 有明确的高度（如 `height: 280px`）
- 确认在 `onMounted` 后初始化图表
- 检查是否有 `window.addEventListener('resize', ...)` 导致尺寸计算问题

## 数据库问题

### 重置数据库
```bash
mysql -u root -p -e "DROP DATABASE campus_device;"
mysql -u root -p < sql/init.sql
```

### 查询慢问题
```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query%';
-- 检查缺少的索引
EXPLAIN SELECT * FROM fault_record WHERE device_id = 1;
```
