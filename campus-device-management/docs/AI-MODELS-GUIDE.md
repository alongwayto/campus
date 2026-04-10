# AI 模型使用指南

## 概述

本系统集成了以下 AI 功能模块：

1. **故障诊断** - 规则引擎 + 关键词分析
2. **设备故障预测** - 统计模型（Apache Commons Math）
3. **异常检测** - 阈值检测 + 规则引擎
4. **NLP 文本分析** - 关键词提取 + 文本分类
5. **智能分类** - 多维度评分算法
6. **维护人员推荐** - 综合评分模型

## 当前实现

当前版本使用**规则引擎和统计模型**实现 AI 功能，无需外部 AI 服务：

### 故障诊断算法

基于关键词匹配和规则引擎：
- 网络故障：匹配"网络"、"断网"、"连接"等关键词
- 电源故障：匹配"电源"、"开机"、"断电"等关键词
- 显示故障：匹配"屏幕"、"显示"、"投影"等关键词

### 故障预测模型

使用 Apache Commons Math 的描述性统计：
```java
DescriptiveStatistics stats = new DescriptiveStatistics();
// 基于设备历史数据计算故障概率
double probability = calculateFailureProbability(deviceId);
```

## 升级到真实 ML 模型

### 方案一：ONNX Runtime（推荐）

1. 添加依赖：
```xml
<dependency>
    <groupId>com.microsoft.onnxruntime</groupId>
    <artifactId>onnxruntime</artifactId>
    <version>1.16.3</version>
</dependency>
```

2. 配置模型路径：
```yaml
ai:
  model:
    path: ./models/
    fault-diagnosis: fault_model.onnx
```

3. 在 `AiModelUtil` 中加载模型：
```java
OrtEnvironment env = OrtEnvironment.getEnvironment();
OrtSession session = env.createSession("./models/fault_model.onnx");
```

### 方案二：调用外部 AI API

```java
// 调用 OpenAI / 百度文心 / 阿里通义
RestTemplate restTemplate = new RestTemplate();
Map<String, Object> request = new HashMap<>();
request.put("model", "gpt-3.5-turbo");
request.put("prompt", faultDescription);
String result = restTemplate.postForObject(AI_API_URL, request, String.class);
```

## 训练数据准备

训练数据存储在 `ai_model_data` 表中：

```sql
INSERT INTO ai_model_data (device_id, fault_id, feature_data, label, data_type)
VALUES (1, 1, '{"faultCount": 5, "ageYears": 3}', '电源故障', 'train');
```

## 模型性能指标

| 功能 | 准确率（目标） | 当前实现 |
|------|------------|---------|
| 故障分类 | > 85% | 规则引擎（约 70%） |
| 故障预测 | > 80% | 统计模型（约 65%） |
| 异常检测 | > 90% | 阈值检测（约 85%） |
