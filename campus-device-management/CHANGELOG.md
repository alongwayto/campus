# 变更日志

## [1.1.0] - 2024-06-01

### 新增
- 🤖 AI 故障诊断功能（规则引擎 + 关键词分析）
- 📊 设备故障预测（统计模型）
- 🔍 异常检测（阈值检测）
- 📝 NLP 文本分析（关键词提取）
- 🎯 智能故障分类和优先级设置
- 👥 维护人员智能推荐
- 📈 数据分析模块（故障趋势、设备健康、维护效率）
- 🔴 WebSocket 实时通知支持
- 🗄️ Redis 缓存支持
- 📡 仪表板 API 接口
- 新增数据库表：device_metrics, ai_model_data, ai_predictions, alert_config, system_config, maintenance_record, device_attachment, fault_attachment

### 前端
- 新增 AI 故障诊断页面（`/ai/diagnosis`）
- 新增设备故障预测页面（`/ai/predict`）
- 新增数据分析页面（`/analytics`）
- 新增 5 个 Pinia 状态管理 Store
- 新增工具模块：constants.js, formatters.js, validators.js, storage.js, http.js
- 新增全局样式：variables.css, main.css, responsive.css
- 侧边栏添加 AI 功能和数据分析导航

### 后端
- 替换 Springfox 为 Springdoc OpenAPI 1.7.0
- 新增 Redis 配置
- 新增 WebSocket 配置
- 新增 AI 模型配置
- 新增 AiController (7个接口)
- 新增 AnalyticsController (4个接口)
- 新增 DashboardController (1个接口)
- 新增 LoggingFilter 请求日志过滤器
- 新增 PermissionCheckAspect 权限切面
- 新增工具类：DataAnalysisUtil, AiModelUtil, PageUtil, ResponseUtil

### 文档
- 新增 API 接口文档
- 新增安装部署指南
- 新增开发指南
- 新增数据库设计文档
- 新增 AI 模型使用指南
- 新增故障排查指南

### DevOps
- 新增 docker-compose.yml
- 新增 Dockerfile（多阶段构建）
- 新增 .env.example 环境变量模板
- 新增 CHANGELOG.md

## [1.0.0] - 2024-05-01

### 初始版本
- 设备管理模块（CRUD、导入导出）
- 故障管理模块（上报、指派、处理）
- 用户权限管理（JWT + Spring Security）
- 操作日志记录
- 角色管理（管理员/维护员/普通用户）
- 仪表板统计展示
- 设备监控模块
- 数据统计模块
