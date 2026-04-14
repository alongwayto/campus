<template>
  <div class="ai-diagnosis">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">AI 故障诊断</span>
          <el-tag type="success" size="small">智能分析</el-tag>
        </div>
      </template>

      <!-- Fault Selection -->
      <div class="select-section">
        <el-alert type="info" :closable="false" show-icon>
          选择一条故障记录，AI 将自动分析故障原因并提供诊断建议。
        </el-alert>
        <div class="select-row">
          <el-select v-model="selectedFaultId" placeholder="选择故障记录" filterable class="fault-select" size="large">
            <el-option
              v-for="fault in faultList"
              :key="fault.id"
              :label="`#${fault.id} - ${fault.title}`"
              :value="fault.id"
            />
          </el-select>
          <el-button type="primary" size="large" :loading="loading" :disabled="!selectedFaultId" @click="runDiagnosis">
            <el-icon><MagicStick /></el-icon>
            开始诊断
          </el-button>
        </div>
      </div>

      <!-- Diagnosis Results -->
      <div v-if="result" class="result-section">
        <el-divider content-position="left">诊断结果</el-divider>

        <el-row :gutter="16" class="info-row">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">设备名称</span>
              <span class="value">{{ result.deviceName }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">故障标题</span>
              <span class="value">{{ result.faultTitle }}</span>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="info-item">
              <span class="label">紧急程度</span>
              <el-tag :type="urgencyType(result.urgencyLevel)">{{ result.urgencyLevel }}</el-tag>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="info-item">
              <span class="label">总体置信度</span>
              <el-progress :percentage="Math.round(result.overallConfidence * 100)" :color="progressColor" :stroke-width="10" />
            </div>
          </el-col>
        </el-row>

        <!-- Diagnoses List -->
        <h4 class="section-title">可能原因分析</h4>
        <el-collapse v-model="activeCollapse">
          <el-collapse-item
            v-for="(item, idx) in result.diagnoses"
            :key="idx"
            :name="idx"
          >
            <template #title>
              <div class="collapse-title">
                <el-tag :type="idx === 0 ? 'danger' : idx === 1 ? 'warning' : 'info'" size="small">
                  {{ idx === 0 ? '最可能' : `可能性 ${idx + 1}` }}
                </el-tag>
                <span class="cause-text">{{ item.possibleCause }}</span>
                <span class="confidence-text">置信度: {{ Math.round(item.confidence * 100) }}%</span>
              </div>
            </template>
            <div class="diagnosis-detail">
              <p><strong>故障类别：</strong>{{ item.category }}</p>
              <p><strong>排查步骤：</strong></p>
              <ol>
                <li v-for="(step, si) in item.troubleshootingSteps" :key="si">{{ step }}</li>
              </ol>
            </div>
          </el-collapse-item>
        </el-collapse>

        <!-- Recommended Actions -->
        <h4 class="section-title">推荐措施</h4>
        <el-timeline>
          <el-timeline-item
            v-for="(action, idx) in result.recommendedActions"
            :key="idx"
            :color="idx === 0 ? '#409EFF' : '#909399'"
          >
            {{ action }}
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAiDiagnosis } from '../../api/auth'
import request from '../../api/request'

const selectedFaultId = ref(null)
const loading = ref(false)
const result = ref(null)
const faultList = ref([])
const activeCollapse = ref([0])

const progressColor = [
  { color: '#f56c6c', percentage: 30 },
  { color: '#e6a23c', percentage: 60 },
  { color: '#67c23a', percentage: 100 }
]

function urgencyType(level) {
  if (level === '高' || level === 'high') return 'danger'
  if (level === '中' || level === 'medium') return 'warning'
  return 'info'
}

async function loadFaults() {
  try {
    const res = await request.get('/faults', { params: { pageNum: 1, pageSize: 100 } })
    const data = res?.data || res
    faultList.value = data?.records || data?.list || data || []
  } catch {
    faultList.value = []
  }
}

async function runDiagnosis() {
  if (!selectedFaultId.value) return
  loading.value = true
  result.value = null
  try {
    const res = await getAiDiagnosis(selectedFaultId.value)
    result.value = res?.data || res
    activeCollapse.value = [0]
  } catch {
    ElMessage.error('诊断失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFaults()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.select-section {
  margin-bottom: 24px;
}

.select-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
}

.fault-select {
  flex: 1;
}

.info-row {
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item .label {
  font-size: 12px;
  color: #909399;
}

.info-item .value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.section-title {
  font-size: 15px;
  color: #303133;
  margin: 20px 0 12px;
}

.collapse-title {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.cause-text {
  flex: 1;
  font-weight: 500;
}

.confidence-text {
  color: #909399;
  font-size: 13px;
}

.diagnosis-detail {
  padding: 0 16px;
  color: #606266;
}

.diagnosis-detail ol {
  padding-left: 20px;
}

.diagnosis-detail li {
  line-height: 2;
}
</style>
