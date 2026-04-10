# 数据库设计文档

## 概述

数据库名称：`campus_device`  
字符集：`utf8mb4`  
排序规则：`utf8mb4_unicode_ci`

## 表结构

### 核心业务表

| 表名 | 说明 |
|------|------|
| role | 角色表 |
| user | 用户表 |
| device_type | 设备类型表 |
| device | 设备表 |
| fault_record | 故障记录表 |
| operation_log | 操作日志表 |

### AI 和扩展表

| 表名 | 说明 |
|------|------|
| device_metrics | 设备实时指标 |
| ai_model_data | AI训练数据 |
| ai_predictions | AI预测结果 |
| alert_config | 告警配置 |
| system_config | 系统配置 |
| maintenance_record | 维护记录 |
| device_attachment | 设备附件 |
| fault_attachment | 故障附件 |

## 核心表设计

### device 设备表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| name | varchar(200) | 设备名称 |
| device_type_id | bigint | 设备类型ID |
| location | varchar(200) | 位置 |
| status | tinyint | 0=离线,1=在线,2=故障 |
| serial_number | varchar(100) | 序列号 |
| manufacturer | varchar(100) | 厂商 |
| purchase_date | date | 采购日期 |
| warranty_expiry | date | 保修到期日 |

### fault_record 故障记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| device_id | bigint | 设备ID |
| reporter_id | bigint | 上报人ID |
| assignee_id | bigint | 处理人ID |
| title | varchar(200) | 故障标题 |
| severity | tinyint | 1=低,2=中,3=高 |
| status | tinyint | 0=待处理,1=已指派,2=处理中,3=已解决,4=已关闭 |
| reported_at | datetime | 上报时间 |
| resolved_at | datetime | 解决时间 |

### ai_predictions AI预测结果表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| device_id | bigint | 设备ID |
| prediction_type | varchar(50) | 预测类型 |
| probability | decimal(5,4) | 概率值(0-1) |
| severity | varchar(20) | 风险等级 |
| recommendation | text | 建议措施 |
| model_version | varchar(50) | 模型版本 |
| predicted_at | datetime | 预测时间 |

## ER 关系图

```
user ─────────────┐
                  │ reporter_id / assignee_id
device_type ──► device ──────────────► fault_record
                  │                         │
                  │                    ai_predictions
                  │                    device_metrics
                  │                    maintenance_record
                  │                    device_attachment
                  └──────────────────► fault_attachment
```
