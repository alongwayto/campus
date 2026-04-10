# API 接口文档

## 概述

智能校园设备管理系统 RESTful API 文档。

**Base URL**: `http://localhost:8081/api`  
**认证方式**: Bearer Token (JWT)  
**文档地址**: `http://localhost:8081/swagger-ui.html`

---

## 认证接口 `/api/auth`

### POST /api/auth/login - 用户登录
**请求体**:
```json
{ "username": "admin", "password": "admin123" }
```
**响应**:
```json
{ "code": 200, "data": { "token": "eyJ...", "username": "admin", "role": "ROLE_ADMIN" } }
```

### POST /api/auth/logout - 用户登出

---

## 设备管理 `/api/devices`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/devices | 分页查询设备列表 |
| GET | /api/devices/{id} | 获取设备详情 |
| POST | /api/devices | 新增设备 |
| PUT | /api/devices/{id} | 更新设备 |
| DELETE | /api/devices/{id} | 删除设备 |
| GET | /api/devices/export | 导出设备列表(Excel) |
| POST | /api/devices/import | 批量导入设备 |

**查询参数**: `name`, `type`, `status`, `page`, `size`

---

## 故障管理 `/api/faults`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/faults | 分页查询故障列表 |
| GET | /api/faults/{id} | 获取故障详情 |
| POST | /api/faults | 上报故障 |
| PUT | /api/faults/{id} | 更新故障 |
| PUT | /api/faults/{id}/assign | 指派维护人员 |
| PUT | /api/faults/{id}/resolve | 解决故障 |
| PUT | /api/faults/{id}/close | 关闭故障 |

---

## AI 智能功能 `/api/ai`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/ai/diagnose | 故障智能诊断 |
| GET | /api/ai/predict/{deviceId} | 设备故障预测 |
| POST | /api/ai/predict/batch | 批量设备故障预测 |
| POST | /api/ai/anomaly/{deviceId} | 异常检测 |
| GET | /api/ai/recommend-maintainer/{faultId} | 推荐维护人员 |
| POST | /api/ai/analyze-text | NLP文本分析 |
| POST | /api/ai/categorize | 智能分类 |

### POST /api/ai/diagnose 示例

**请求**:
```json
{ "description": "设备无法开机，电源指示灯不亮", "deviceId": 1 }
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "faultType": "电源故障",
    "severity": "高",
    "confidence": 0.85,
    "diagnosisSteps": ["1. 检查电源连接", "2. 检查电源模块"],
    "solutions": ["更换电源适配器", "联系厂家售后"],
    "estimatedTime": "1-2小时",
    "requiresExpert": true
  }
}
```

---

## 数据分析 `/api/analytics`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/analytics | 综合分析数据 |
| GET | /api/analytics/fault-trend | 故障趋势（近N月） |
| GET | /api/analytics/device-health | 设备健康报告 |
| GET | /api/analytics/maintenance-efficiency | 维护效率分析 |

---

## 仪表板 `/api/dashboard`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/dashboard | 仪表板统计数据 |

---

## 用户管理 `/api/users`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 分页查询用户 |
| POST | /api/users | 新增用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证/Token过期 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```
