<template>
  <div class="ai-prediction">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">AI 设备健康预测</span>
          <el-tag type="success" size="small">智能预测</el-tag>
        </div>
      </template>

      <!-- Device Selection -->
      <div class="select-section">
        <el-alert type="info" :closable="false" show-icon>
          选择设备，AI 将基于历史数据分析设备健康状况，并预测未来故障概率。
        </el-alert>
        <div class="select-row">
          <el-select v-model="selectedDeviceId" placeholder="选择设备" filterable class="device-select" size="large">
            <el-option
              v-for="device in deviceList"
              :key="device.id"
              :label="device.name"
              :value="device.id"
            />
          </el-select>
          <el-button type="primary" size="large" :loading="loading" :disabled="!selectedDeviceId" @click="runPrediction">
            <el-icon><TrendCharts /></el-icon>
            开始预测
          </el-button>
        </div>
      </div>

      <!-- Prediction Results -->
      <div v-if="result" class="result-section">
        <el-divider content-position="left">预测结果</el-divider>

        <!-- Health Score -->
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="health-card">
              <h4>设备健康度</h4>
              <el-progress type="dashboard" :percentage="result.healthScore" :color="healthColor" :stroke-width="12" :width="140">
                <template #default>
                  <div class="health-inner">
                    <span class="health-score">{{ result.healthScore }}</span>
                    <span class="health-label">{{ result.healthLevel }}</span>
                  </div>
                </template>
              </el-progress>
            </div>
          </el-col>

          <el-col :span="16">
            <div class="prediction-cards">
              <div class="pred-card">
                <span class="pred-label">7天内故障概率</span>
                <el-progress :percentage="Math.round(result.failureProbability7Days * 100)" :color="probColor(result.failureProbability7Days)" :stroke-width="8" />
              </div>
              <div class="pred-card">
                <span class="pred-label">30天内故障概率</span>
                <el-progress :percentage="Math.round(result.failureProbability30Days * 100)" :color="probColor(result.failureProbability30Days)" :stroke-width="8" />
              </div>
              <div class="pred-card">
                <span class="pred-label">90天内故障概率</span>
                <el-progress :percentage="Math.round(result.failureProbability90Days * 100)" :color="probColor(result.failureProbability90Days)" :stroke-width="8" />
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- Risk & Info -->
        <el-row :gutter="20" class="detail-row">
          <el-col :span="8">
            <el-card shadow="never" class="detail-card">
              <h4>风险等级</h4>
              <el-tag :type="riskType(result.riskLevel)" size="large">{{ result.riskLevel }}</el-tag>
              <p class="detail-info">预计剩余寿命：{{ result.estimatedRemainingLife }}</p>
              <p class="detail-info">建议维保时间：{{ result.nextMaintenanceRecommendation }}</p>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="never" class="detail-card">
              <h4>风险因素</h4>
              <ul class="risk-list">
                <li v-for="(factor, idx) in result.riskFactors" :key="idx">
                  <el-icon color="#e6a23c"><Warning /></el-icon>
                  {{ factor }}
                </li>
              </ul>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="never" class="detail-card">
              <h4>预防性建议</h4>
              <ul class="action-list">
                <li v-for="(action, idx) in result.preventiveActions" :key="idx">
                  <el-icon color="#67c23a"><CircleCheck /></el-icon>
                  {{ action }}
                </li>
              </ul>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAiPrediction } from '../../api/auth'
import request from '../../api/request'

const selectedDeviceId = ref(null)
const loading = ref(false)
const result = ref(null)
const deviceList = ref([])

const healthColor = [
  { color: '#f56c6c', percentage: 30 },
  { color: '#e6a23c', percentage: 60 },
  { color: '#67c23a', percentage: 100 }
]

function probColor(prob) {
  if (prob > 0.6) return '#f56c6c'
  if (prob > 0.3) return '#e6a23c'
  return '#67c23a'
}

function riskType(level) {
  if (level === '高风险' || level === 'high') return 'danger'
  if (level === '中风险' || level === 'medium') return 'warning'
  return 'success'
}

async function loadDevices() {
  try {
    const res = await request.get('/devices', { params: { pageNum: 1, pageSize: 100 } })
    const data = res?.data || res
    deviceList.value = data?.records || data?.list || data || []
  } catch {
    deviceList.value = []
  }
}

async function runPrediction() {
  if (!selectedDeviceId.value) return
  loading.value = true
  result.value = null
  try {
    const res = await getAiPrediction(selectedDeviceId.value)
    result.value = res?.data || res
  } catch {
    ElMessage.error('预测失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDevices()
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

.device-select {
  flex: 1;
}

.health-card {
  text-align: center;
  padding: 16px;
}

.health-card h4 {
  margin: 0 0 16px;
  color: #303133;
}

.health-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.health-score {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.health-label {
  font-size: 12px;
  color: #909399;
}

.prediction-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 16px 0;
}

.pred-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pred-label {
  font-size: 13px;
  color: #606266;
}

.detail-row {
  margin-top: 24px;
}

.detail-card {
  height: 100%;
}

.detail-card h4 {
  margin: 0 0 12px;
  font-size: 14px;
  color: #303133;
}

.detail-info {
  font-size: 13px;
  color: #606266;
  margin: 8px 0;
}

.risk-list, .action-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.risk-list li, .action-list li {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 0;
  font-size: 13px;
  color: #606266;
}
</style>
