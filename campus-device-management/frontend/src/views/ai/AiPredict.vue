<template>
  <div class="ai-predict page-card">
    <div class="page-header">
      <h2 class="page-title">📊 设备故障预测</h2>
      <div>
        <el-button type="primary" :loading="batchLoading" @click="batchPredict">
          批量预测所有设备
        </el-button>
      </div>
    </div>

    <!-- Risk Summary Cards -->
    <el-row :gutter="16" class="risk-summary" v-if="predictions.length > 0">
      <el-col :span="8">
        <div class="risk-card risk-high">
          <div class="risk-count">{{ highRiskCount }}</div>
          <div class="risk-label">高风险设备</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="risk-card risk-medium">
          <div class="risk-count">{{ mediumRiskCount }}</div>
          <div class="risk-label">中风险设备</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="risk-card risk-low">
          <div class="risk-count">{{ lowRiskCount }}</div>
          <div class="risk-label">低风险设备</div>
        </div>
      </el-col>
    </el-row>

    <!-- Single Device Prediction -->
    <el-card style="margin-bottom: 20px">
      <template #header><span>单设备预测</span></template>
      <el-row :gutter="16" align="middle">
        <el-col :span="8">
          <el-select v-model="selectedDeviceId" placeholder="选择要预测的设备" style="width: 100%">
            <el-option
              v-for="d in deviceOptions"
              :key="d.value"
              :label="d.label"
              :value="d.value"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" :loading="singleLoading" @click="singlePredict" :disabled="!selectedDeviceId">
            预测故障概率
          </el-button>
        </el-col>
        <el-col :span="12" v-if="singleResult">
          <el-card class="single-result">
            <div class="result-row">
              <span>设备：<strong>{{ singleResult.deviceName }}</strong></span>
              <el-tag :type="riskType(singleResult.riskLevel)">{{ singleResult.riskLevel }}</el-tag>
            </div>
            <div class="result-row">
              <span>故障概率：</span>
              <el-progress
                :percentage="Math.round(singleResult.failureProbability * 100)"
                :color="progressColor(singleResult.failureProbability)"
                style="width: 200px"
              />
            </div>
            <div class="result-row">
              <span>预测结论：{{ singleResult.predictedIssue }}</span>
            </div>
            <div v-if="singleResult.recommendations?.length" class="recommendations">
              <strong>建议措施：</strong>
              <ul>
                <li v-for="r in singleResult.recommendations" :key="r">{{ r }}</li>
              </ul>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- Batch Prediction Results -->
    <el-card v-if="predictions.length > 0">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>批量预测结果</span>
          <el-select v-model="filterRisk" placeholder="筛选风险等级" clearable style="width: 150px" size="small">
            <el-option label="高风险" value="高风险" />
            <el-option label="中风险" value="中风险" />
            <el-option label="低风险" value="低风险" />
          </el-select>
        </div>
      </template>

      <el-table :data="filteredPredictions" stripe>
        <el-table-column prop="deviceName" label="设备名称" min-width="160" />
        <el-table-column label="故障概率" width="200">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.round(row.failureProbability * 100)"
              :color="progressColor(row.failureProbability)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="riskType(row.riskLevel)">{{ row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="predictedIssue" label="预测结论" min-width="200" show-overflow-tooltip />
        <el-table-column prop="predictedAt" label="预测时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="showDetail(row)">详情</el-button>
            <el-button size="small" type="warning" text @click="createWorkOrder(row)">创建工单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Feature Importance Chart -->
    <el-card v-if="showFeatureChart" style="margin-top: 20px">
      <template #header><span>特征重要性分析</span></template>
      <div ref="featureChartRef" style="height: 280px"></div>
    </el-card>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="预测详情" width="600px">
      <div v-if="detailData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备名称" :span="2">{{ detailData.deviceName }}</el-descriptions-item>
          <el-descriptions-item label="故障概率">{{ Math.round(detailData.failureProbability * 100) }}%</el-descriptions-item>
          <el-descriptions-item label="风险等级">
            <el-tag :type="riskType(detailData.riskLevel)">{{ detailData.riskLevel }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预测时间" :span="2">{{ detailData.predictedAt }}</el-descriptions-item>
          <el-descriptions-item label="预测结论" :span="2">{{ detailData.predictedIssue }}</el-descriptions-item>
        </el-descriptions>
        <el-divider>风险特征</el-divider>
        <el-timeline>
          <el-timeline-item v-for="s in detailData.symptoms" :key="s" type="warning">{{ s }}</el-timeline-item>
        </el-timeline>
        <el-divider>建议措施</el-divider>
        <el-timeline>
          <el-timeline-item v-for="r in detailData.recommendations" :key="r" type="primary">{{ r }}</el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { predictDeviceFailure, batchPredictFailure } from '../../api/ai'
import { useRouter } from 'vue-router'

const router = useRouter()
const batchLoading = ref(false)
const singleLoading = ref(false)
const selectedDeviceId = ref(null)
const singleResult = ref(null)
const predictions = ref([])
const filterRisk = ref('')
const detailVisible = ref(false)
const detailData = ref(null)
const featureChartRef = ref(null)
const showFeatureChart = ref(false)
let featureChart = null

const deviceOptions = [
  { label: '教学楼A-101 教师机', value: 1 },
  { label: '图书馆查询终端-01', value: 2 },
  { label: '行政楼核心交换机', value: 3 },
  { label: '宿舍楼B区无线AP-03', value: 4 },
  { label: '行政办公室激光打印机', value: 5 }
]

const allDeviceIds = deviceOptions.map(d => d.value)

const highRiskCount = computed(() => predictions.value.filter(p => p.riskLevel === '高风险').length)
const mediumRiskCount = computed(() => predictions.value.filter(p => p.riskLevel === '中风险').length)
const lowRiskCount = computed(() => predictions.value.filter(p => p.riskLevel === '低风险').length)

const filteredPredictions = computed(() => {
  if (!filterRisk.value) return predictions.value
  return predictions.value.filter(p => p.riskLevel === filterRisk.value)
})

function riskType(level) {
  const map = { '高风险': 'danger', '中风险': 'warning', '低风险': 'success' }
  return map[level] || 'info'
}

function progressColor(prob) {
  if (prob >= 0.7) return '#F56C6C'
  if (prob >= 0.4) return '#E6A23C'
  return '#67C23A'
}

async function singlePredict() {
  singleLoading.value = true
  try {
    const res = await predictDeviceFailure(selectedDeviceId.value)
    singleResult.value = res
    if (res.featureImportance) {
      showFeatureChart.value = true
      setTimeout(() => initFeatureChart(res.featureImportance), 100)
    }
  } catch (e) {
    ElMessage.error('预测失败')
  } finally {
    singleLoading.value = false
  }
}

async function batchPredict() {
  batchLoading.value = true
  try {
    const res = await batchPredictFailure(allDeviceIds)
    predictions.value = res
    ElMessage.success(`已完成 ${res.length} 台设备的故障预测`)
  } catch (e) {
    ElMessage.error('批量预测失败')
  } finally {
    batchLoading.value = false
  }
}

function showDetail(row) {
  detailData.value = row
  detailVisible.value = true
}

function createWorkOrder(row) {
  router.push({ path: '/faults', query: { action: 'create', deviceId: row.deviceId } })
}

function initFeatureChart(featureImportance) {
  if (!featureChartRef.value) return
  featureChart = echarts.init(featureChartRef.value)
  const keys = Object.keys(featureImportance)
  const values = Object.values(featureImportance).map(v => Math.round(v * 100))
  featureChart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}: {c}%' },
    xAxis: { type: 'category', data: keys },
    yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
    series: [{
      type: 'bar',
      data: values,
      itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] }
    }]
  })
}

onBeforeUnmount(() => featureChart?.dispose())
</script>

<style scoped>
.ai-predict { display: flex; flex-direction: column; gap: 20px; }
.risk-summary { margin-bottom: 4px; }
.risk-card {
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  color: white;
}
.risk-high { background: linear-gradient(135deg, #F56C6C, #c0392b); }
.risk-medium { background: linear-gradient(135deg, #E6A23C, #d68910); }
.risk-low { background: linear-gradient(135deg, #67C23A, #27ae60); }
.risk-count { font-size: 40px; font-weight: 700; }
.risk-label { font-size: 14px; opacity: 0.9; margin-top: 4px; }
.single-result { background: #f8f9fa; }
.result-row { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.recommendations ul { margin: 4px 0 0 16px; }
.recommendations li { margin-bottom: 4px; }
</style>
