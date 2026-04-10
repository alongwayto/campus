<template>
  <div class="ai-diagnosis page-card">
    <div class="page-header">
      <h2 class="page-title">🤖 AI 故障诊断</h2>
      <el-tag type="success">AI 服务运行中</el-tag>
    </div>

    <el-row :gutter="20">
      <!-- Input Panel -->
      <el-col :span="12">
        <el-card class="input-card">
          <template #header>
            <span>故障描述输入</span>
          </template>
          <el-form :model="form" label-position="top">
            <el-form-item label="选择设备（可选）">
              <el-select v-model="form.deviceId" placeholder="请选择设备" clearable style="width: 100%">
                <el-option
                  v-for="device in deviceOptions"
                  :key="device.value"
                  :label="device.label"
                  :value="device.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="故障描述">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="6"
                placeholder="请详细描述故障现象，例如：设备无法开机，按下电源键后无任何反应，电源指示灯不亮..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="diagnosing"
                :disabled="!form.description"
                @click="handleDiagnose"
                icon="MagicStick"
                style="width: 100%"
              >
                {{ diagnosing ? 'AI 诊断中...' : '开始 AI 诊断' }}
              </el-button>
            </el-form-item>
          </el-form>

          <!-- Quick Examples -->
          <div class="quick-examples">
            <div class="examples-title">快速示例：</div>
            <el-space wrap>
              <el-tag
                v-for="example in examples"
                :key="example"
                class="example-tag"
                @click="form.description = example"
                style="cursor: pointer"
              >{{ example.substring(0, 15) }}...</el-tag>
            </el-space>
          </div>
        </el-card>
      </el-col>

      <!-- Result Panel -->
      <el-col :span="12">
        <el-card class="result-card">
          <template #header>
            <span>诊断结果</span>
          </template>

          <div v-if="!result && !diagnosing" class="empty-state">
            <el-empty description="请输入故障描述后点击诊断" :image-size="100" />
          </div>

          <div v-if="diagnosing" class="ai-loading">
            <el-icon class="is-loading" :size="32"><Loading /></el-icon>
            <span>AI 正在分析故障...</span>
          </div>

          <div v-if="result && !diagnosing" class="diagnosis-result">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="故障类型">
                <el-tag type="primary">{{ result.faultType }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="严重程度">
                <el-tag :type="severityType(result.severity)">{{ result.severity }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="诊断置信度">
                <el-progress :percentage="Math.round(result.confidence * 100)" :stroke-width="8" />
              </el-descriptions-item>
              <el-descriptions-item label="预计修复时间">
                {{ result.estimatedTime }}
              </el-descriptions-item>
              <el-descriptions-item label="是否需要专家" :span="2">
                <el-tag :type="result.requiresExpert ? 'danger' : 'success'">
                  {{ result.requiresExpert ? '是，建议联系专业技术人员' : '否，一般维护人员可处理' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <el-divider>诊断步骤</el-divider>
            <el-steps direction="vertical" :active="result.diagnosisSteps?.length" :space="40">
              <el-step
                v-for="(step, index) in result.diagnosisSteps"
                :key="index"
                :title="step"
                status="process"
              />
            </el-steps>

            <el-divider>推荐解决方案</el-divider>
            <el-timeline>
              <el-timeline-item
                v-for="(solution, index) in result.solutions"
                :key="index"
                :timestamp="`方案 ${index + 1}`"
                placement="top"
                type="primary"
              >
                {{ solution }}
              </el-timeline-item>
            </el-timeline>

            <div class="result-actions">
              <el-button type="primary" @click="createFaultRecord">基于此诊断创建故障单</el-button>
              <el-button @click="result = null">清除结果</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- NLP Analysis Section -->
    <el-card class="nlp-card" style="margin-top: 20px">
      <template #header>
        <span>📝 NLP 文本分析</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-input v-model="nlpText" type="textarea" :rows="3" placeholder="输入故障描述进行关键词提取和分类分析..." />
          <el-button type="primary" style="margin-top: 12px" :loading="analyzing" @click="handleNlpAnalyze">
            分析文本
          </el-button>
        </el-col>
        <el-col :span="8" v-if="nlpResult">
          <div class="nlp-result">
            <div class="nlp-section">
              <strong>关键词：</strong>
              <el-space wrap style="margin-top: 8px">
                <el-tag v-for="kw in nlpResult.keywords" :key="kw" size="small">{{ kw }}</el-tag>
              </el-space>
            </div>
            <div class="nlp-section">
              <strong>摘要：</strong>
              <p style="margin-top: 4px; color: #606266">{{ nlpResult.summary }}</p>
            </div>
            <div class="nlp-section">
              <strong>分类：</strong>
              <el-tag type="primary" style="margin-top: 4px">{{ nlpResult.category }}</el-tag>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { diagnoseFault, analyzeText } from '../../api/ai'
import { useRouter } from 'vue-router'

const router = useRouter()
const diagnosing = ref(false)
const analyzing = ref(false)
const result = ref(null)
const nlpResult = ref(null)
const nlpText = ref('')

const form = reactive({
  deviceId: null,
  description: ''
})

const deviceOptions = [
  { label: '图书馆查询终端-01', value: 2 },
  { label: '行政楼核心交换机', value: 3 },
  { label: '实验楼门禁系统-01', value: 8 }
]

const examples = [
  '设备无法开机，按下电源键后无任何反应，电源指示灯不亮，已尝试更换插座无效',
  '网络突然断连，重启路由器无效，其他设备正常，疑似本机网卡故障',
  '打印机频繁卡纸，且提示墨粉不足，已清理卡纸但问题依旧'
]

function severityType(severity) {
  const map = { '高': 'danger', '中': 'warning', '低': 'info' }
  return map[severity] || 'info'
}

async function handleDiagnose() {
  if (!form.description.trim()) {
    ElMessage.warning('请输入故障描述')
    return
  }
  diagnosing.value = true
  result.value = null
  try {
    const res = await diagnoseFault({
      description: form.description,
      deviceId: form.deviceId
    })
    result.value = res
    ElMessage.success('诊断完成')
  } catch (e) {
    ElMessage.error('诊断失败，请稍后重试')
  } finally {
    diagnosing.value = false
  }
}

async function handleNlpAnalyze() {
  if (!nlpText.value.trim()) {
    ElMessage.warning('请输入文本')
    return
  }
  analyzing.value = true
  try {
    const res = await analyzeText(nlpText.value)
    nlpResult.value = res
  } catch (e) {
    ElMessage.error('分析失败')
  } finally {
    analyzing.value = false
  }
}

function createFaultRecord() {
  router.push({ path: '/faults', query: { action: 'create', description: form.description } })
}
</script>

<style scoped>
.ai-diagnosis { display: flex; flex-direction: column; gap: 0; }
.input-card, .result-card { height: 100%; }
.empty-state { padding: 40px 0; }
.ai-loading { display: flex; align-items: center; justify-content: center; gap: 12px; padding: 60px 0; color: #909399; font-size: 16px; }
.diagnosis-result { padding: 8px 0; }
.result-actions { display: flex; gap: 12px; margin-top: 20px; }
.quick-examples { margin-top: 16px; padding-top: 12px; border-top: 1px solid #eee; }
.examples-title { font-size: 12px; color: #909399; margin-bottom: 8px; }
.example-tag { max-width: 120px; overflow: hidden; text-overflow: ellipsis; }
.nlp-result { padding: 12px; background: #f5f7fa; border-radius: 6px; height: 100%; }
.nlp-section { margin-bottom: 12px; }
</style>
