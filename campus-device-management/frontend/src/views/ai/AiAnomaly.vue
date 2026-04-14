<template>
  <div class="ai-anomaly">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">AI 异常检测</span>
          <el-button type="primary" :loading="loading" @click="loadAnomalies">
            <el-icon><Refresh /></el-icon>
            刷新检测
          </el-button>
        </div>
      </template>

      <el-alert type="info" :closable="false" show-icon class="info-alert">
        AI 自动监控所有设备状态，检测异常模式并发出预警。
      </el-alert>

      <!-- Summary Stats -->
      <el-row :gutter="16" class="summary-row">
        <el-col :span="6">
          <div class="summary-card" style="border-left-color: #f56c6c">
            <div class="summary-value">{{ highCount }}</div>
            <div class="summary-label">高风险告警</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-card" style="border-left-color: #e6a23c">
            <div class="summary-value">{{ mediumCount }}</div>
            <div class="summary-label">中风险告警</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-card" style="border-left-color: #409eff">
            <div class="summary-value">{{ lowCount }}</div>
            <div class="summary-label">低风险告警</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-card" style="border-left-color: #67c23a">
            <div class="summary-value">{{ anomalies.length }}</div>
            <div class="summary-label">总告警数</div>
          </div>
        </el-col>
      </el-row>

      <!-- Anomaly List -->
      <el-table :data="anomalies" stripe v-loading="loading">
        <el-table-column prop="deviceName" label="设备名称" min-width="160" />
        <el-table-column prop="anomalyType" label="异常类型" width="140">
          <template #default="{ row }">
            <el-tag size="small">{{ row.anomalyType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="severityType(row.severity)" size="small">{{ row.severity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="异常描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="recommendation" label="处理建议" min-width="200" show-overflow-tooltip />
        <el-table-column prop="detectedAt" label="检测时间" width="160" />
      </el-table>

      <el-empty v-if="!loading && anomalies.length === 0" description="当前没有检测到异常" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAiAnomalies } from '../../api/auth'

const loading = ref(false)
const anomalies = ref([])

const highCount = computed(() => anomalies.value.filter(a => a.severity === '高' || a.severity === 'high').length)
const mediumCount = computed(() => anomalies.value.filter(a => a.severity === '中' || a.severity === 'medium').length)
const lowCount = computed(() => anomalies.value.filter(a => a.severity === '低' || a.severity === 'low').length)

function severityType(s) {
  if (s === '高' || s === 'high') return 'danger'
  if (s === '中' || s === 'medium') return 'warning'
  return 'info'
}

async function loadAnomalies() {
  loading.value = true
  try {
    const res = await getAiAnomalies()
    anomalies.value = res?.data || res || []
  } catch {
    ElMessage.error('检测失败，请稍后重试')
    anomalies.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAnomalies()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.info-alert {
  margin-bottom: 20px;
}

.summary-row {
  margin-bottom: 24px;
}

.summary-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  border-left: 4px solid;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.summary-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
</style>
