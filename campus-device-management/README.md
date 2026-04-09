# 智能校园设备管理系统

> 基于 Spring Boot + Vue 3 构建的校园设备全生命周期管理平台，覆盖设备台账、故障上报、状态监控、数据分析与权限管理五大核心模块。

---

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.x |
| 安全认证 | Spring Security + JWT | — |
| 持久层 | MyBatis-Plus | 3.5.x |
| 数据库 | MySQL | 8.0+ |
| 接口文档 | Springdoc OpenAPI (Swagger UI) | 2.x |
| 前端框架 | Vue | 3.x |
| UI 组件库 | Element Plus | 2.x |
| 构建工具 | Vite | 4.x |
| HTTP 客户端 | Axios | 1.x |
| 状态管理 | Pinia | 2.x |

---

## 目录结构

```
campus-device-management/
├── backend/                     # Spring Boot 后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/campus/device/
│   │   │   │   ├── config/      # 安全、Swagger 等配置
│   │   │   │   ├── controller/  # REST 控制器
│   │   │   │   ├── service/     # 业务逻辑层
│   │   │   │   ├── mapper/      # MyBatis-Plus Mapper
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── vo/          # 视图对象
│   │   │   │   └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── mapper/      # XML 映射文件
│   │   └── test/
│   └── pom.xml
├── frontend/                    # Vue 3 前端
│   ├── src/
│   │   ├── api/                 # 接口请求模块
│   │   ├── assets/              # 静态资源
│   │   ├── components/          # 公共组件
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── views/               # 页面视图
│   │   └── main.js
│   ├── index.html
│   └── package.json
├── sql/
│   └── init.sql                 # 数据库初始化脚本
├── docs/
│   ├── deployment.md            # 部署指南
│   └── user-manual.md           # 用户手册
└── README.md
```

---

## 功能模块

| 模块 | 功能说明 |
|------|---------|
| 🔐 用户认证 | JWT 登录/登出、角色鉴权（管理员/维护员/普通用户） |
| 📦 设备管理 | 设备台账增删改查、按类型/位置/状态筛选、设备详情 |
| 🔧 故障管理 | 故障上报、指派维护员、处理流程跟踪、历史记录 |
| 📊 状态监控 | 实时设备状态统计、在线/离线/故障数量看板 |
| 📈 数据分析 | 故障趋势图、设备类型分布、维修响应时长统计 |
| ⚙️ 系统管理 | 用户管理、角色管理、操作日志查询 |

---

## 快速启动

### 1. 数据库配置

```bash
# 登录 MySQL（8.0+）
mysql -u root -p

# 导入初始化脚本
source /path/to/campus-device-management/sql/init.sql
```

或直接执行：

```bash
mysql -u root -p < sql/init.sql
```

### 2. 后端启动

```bash
cd backend

# 修改数据库连接（src/main/resources/application.yml）
# spring.datasource.url / username / password

# 启动应用（默认端口 8080）
mvn spring-boot:run
```

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器（默认端口 3000）
npm run dev
```

### 4. 访问系统

打开浏览器访问：<http://localhost:3000>

### 5. 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 系统管理员 |
| maintainer | maint123 | 维护员 |
| user1 | user123 | 普通用户 |

> ⚠️ **安全提示**：生产环境请务必修改默认密码。

---

## API 文档

后端启动后，Swagger UI 访问地址：

<http://localhost:8080/swagger-ui/index.html>

接口均以 `/api/v1` 为前缀，需在 Swagger 页面点击 **Authorize** 填入登录后获取的 JWT Token（格式：`Bearer <token>`）。

---

## 截图预览

> 以下为功能截图占位，实际截图请在完整部署后替换。

| 登录页 | 设备列表 |
|--------|---------|
| ![登录页](docs/images/login.png) | ![设备列表](docs/images/device-list.png) |

| 故障详情 | 数据分析 |
|---------|---------|
| ![故障详情](docs/images/fault-detail.png) | ![数据分析](docs/images/dashboard.png) |

---

## 许可证

本项目仅供学习与研究使用。
