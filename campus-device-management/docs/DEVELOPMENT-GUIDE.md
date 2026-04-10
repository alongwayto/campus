# 开发指南

## 项目架构

```
campus-device-management/
├── backend/                    # Spring Boot 后端
│   └── src/main/java/com/campus/device/
│       ├── aspect/             # AOP 切面
│       ├── config/             # 配置类
│       ├── controller/         # REST 控制器
│       ├── dao/                # MyBatis Mapper
│       ├── exception/          # 异常处理
│       ├── filter/             # Servlet 过滤器
│       ├── model/
│       │   ├── dto/            # 数据传输对象
│       │   ├── entity/         # 数据库实体
│       │   └── vo/             # 视图对象
│       ├── security/           # JWT 安全配置
│       └── service/            # 业务逻辑层
│           └── impl/
├── frontend/                   # Vue 3 前端
│   └── src/
│       ├── api/                # API 请求模块
│       ├── router/             # 路由配置
│       ├── stores/             # Pinia 状态管理
│       ├── styles/             # 全局样式
│       ├── utils/              # 工具模块
│       └── views/              # 页面组件
│           ├── ai/             # AI 功能页面
│           ├── analytics/      # 数据分析页面
│           ├── device/         # 设备管理页面
│           ├── fault/          # 故障管理页面
│           └── system/         # 系统管理页面
├── sql/                        # 数据库脚本
└── docs/                       # 项目文档
```

## 后端开发规范

### 1. 分层架构
- **Controller**: 只做参数校验和结果返回，不包含业务逻辑
- **Service**: 核心业务逻辑，一个方法做一件事
- **Mapper**: 数据库操作，复杂查询写在 XML 中
- **Entity**: 与数据库表一一对应，不添加业务方法

### 2. 统一响应格式
```java
// 成功
return Result.success(data);

// 失败
return Result.fail("错误信息");
```

### 3. 操作日志
```java
@Log("操作描述")
@PostMapping("/devices")
public Result<Void> addDevice(...) { ... }
```

### 4. 异常处理
```java
// 业务异常
throw new BusinessException("设备不存在");
```

## 前端开发规范

### 1. Composition API 优先
```javascript
// 推荐
const count = ref(0)
const doubled = computed(() => count.value * 2)

// 避免使用 Options API
```

### 2. API 请求
```javascript
// api/device.js 中定义
export function getDevices(params) {
  return request.get('/devices', { params })
}

// 在组件中使用
import { getDevices } from '../../api/device'
const devices = await getDevices({ page: 1 })
```

### 3. 状态管理
```javascript
// stores/useDeviceStore.js
export const useDeviceStore = defineStore('device', () => {
  const devices = ref([])
  // ...
})

// 组件中使用
const deviceStore = useDeviceStore()
```

## AI 功能扩展指南

### 添加新的 AI 分析功能

1. 在 `AiDiagnosisService` 接口中添加方法
2. 在 `AiDiagnosisServiceImpl` 中实现（规则引擎或ML模型调用）
3. 在 `AiController` 中添加新的 REST 端点
4. 在前端 `api/ai.js` 中添加对应 API 调用
5. 在 `views/ai/` 中添加或更新 Vue 组件

### 接入真实 AI 模型

1. 将 ONNX 模型文件放置在 `ai.model.path` 配置的路径下
2. 在 `AiModelUtil` 中添加模型加载和推理逻辑
3. 在 `AiModelConfig` 中配置模型路径

## 测试

```bash
# 后端测试
cd backend
mvn test

# 前端构建测试
cd frontend
npm run build
```
