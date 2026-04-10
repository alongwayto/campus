<template>
  <div class="analytics-page page-card">
    <div class="page-header">
      <h2 class="page-title">📈 数据分析</h2>
      <div>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="margin-right: 12px"
          @change="fetchData"
        />
        <el-button type="primary" :loading="loading" @click="fetchData">刷新数据</el-button>
      </div>
    </div>

    <!-- KPI Cards -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :span="6" v-for="kpi in kpiCards" :key="kpi.title">
        <div class="kpi-card" :style="{ borderLeftColor: kpi.color }">
          <div class="kpi-value" :style="{ color: kpi.color }">{{ kpi.value }}</div>
          <div class="kpi-title">{{ kpi.title }}</div>
          <div class="kpi-desc">{{ kpi.desc }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts Row 1 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="14">
        <el-card>
          <template #header><span>故障趋势分析</span></template>
          <div ref="trendChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header><span>设备类型分布</span></template>
          <div ref="typeChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Row 2 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header><span>故障状态分布</span></template>
          <div ref="statusChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>平均修复时间（小时）</span></template>
          <div ref="mttrChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Maintainer Performance -->
    <el-card style="margin-top: 20px">
      <template #header><span>维护人员绩效</span></template>
      <el-table :data="maintainerStats" stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="tasksCompleted" label="完成工单数" width="120" />
        <el-table-column label="平均处理时长" width="140">
          <template #default="{ row }">{{ row.avgTime }}小时</template>
        </el-table-column>
        <el-table-column label="满意度评分" width="200">
          <template #default="{ row }">
            <el-rate :model-value="row.score" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column label="完成率">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(row.tasksCompleted / 35 * 100)" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getAnalyticsData, getFaultTrend, getMaintenanceEfficiency } from '../../api/analytics'

const loading = ref(false)
const dateRange = ref([])
const trendChartRef = ref(null)
const typeChartRef = ref(null)
const statusChartRef = ref(null)
const mttrChartRef = ref(null)
let trendChart, typeChart, statusChart, mttrChart

const maintainerStats = ref([])
const kpiCards = ref([
  { title: '故障解决率', value: '85%', desc: '当月已解决/总故障', color: '#67C23A' },
  { title: '平均响应时间', value: '2.3h', desc: '接单到开始处理', color: '#409EFF' },
  { title: '平均修复时间', value: '5.8h', desc: '开始处理到解决', color: '#E6A23C' },
  { title: '首次修复率', value: '78%', desc: '一次修复成功比例', color: '#F56C6C' }
])

async function fetchData() {
  loading.value = true
  try {
    const [analytics, trend, efficiency] = await Promise.all([
      getAnalyticsData({ startDate: dateRange.value?.[0], endDate: dateRange.value?.[1] }),
      getFaultTrend(12),
      getMaintenanceEfficiency()
    ])

    maintainerStats.value = efficiency.maintainerStats || []

    initTrendChart(trend)
    initTypeChart(analytics.deviceTypeDistribution)
    initStatusChart(analytics.faultStatusDistribution)
    initMttrChart(analytics.avgResolutionTime)
  } catch (e) {
    console.error('Failed to fetch analytics:', e)
    // Use demo data
    initChartsWithDemo()
  } finally {
    loading.value = false
  }
}

function initChartsWithDemo() {
  initTrendChart({
    labels: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
    faultCounts: [12,8,15,10,18,22,16,14,20,17,13,11],
    resolvedCounts: [10,7,14,9,16,20,15,13,18,15,12,10]
  })
  initTypeChart({ '计算机设备': 120, '网络设备': 85, '打印设备': 43, '安防设备': 67, '教学设备': 38 })
  initStatusChart({ '待处理': 15, '处理中': 8, '已解决': 45, '已关闭': 32 })
  initMttrChart({ '网络故障': 2.5, '硬件故障': 8.0, '软件故障': 3.5, '电源故障': 4.0 })
  maintainerStats.value = [{ name: '张维修', tasksCompleted: 28, avgTime: 4.5, score: 4.5 }]
}

function initTrendChart(data) {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['故障数量', '已解决'] },
    xAxis: { type: 'category', data: data.labels || Object.keys(data.faultTrend || {}) },
    yAxis: { type: 'value' },
    series: [
      { name: '故障数量', type: 'line', smooth: true, data: data.faultCounts || Object.values(data.faultTrend || {}), itemStyle: { color: '#F56C6C' }, areaStyle: { color: 'rgba(245,108,108,0.1)' } },
      { name: '已解决', type: 'line', smooth: true, data: data.resolvedCounts || [], itemStyle: { color: '#67C23A' }, areaStyle: { color: 'rgba(103,194,58,0.1)' } }
    ]
  })
}

function initTypeChart(data) {
  if (!typeChartRef.value || !data) return
  typeChart = echarts.init(typeChartRef.value)
  typeChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '45%'],
      data: Object.entries(data).map(([name, value]) => ({ name, value }))
    }]
  })
}

function initStatusChart(data) {
  if (!statusChartRef.value || !data) return
  statusChart = echarts.init(statusChartRef.value)
  const colors = ['#F56C6C', '#E6A23C', '#409EFF', '#67C23A', '#909399']
  statusChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '65%',
      data: Object.entries(data).map(([name, value], i) => ({ name, value, itemStyle: { color: colors[i] } }))
    }]
  })
}

function initMttrChart(data) {
  if (!mttrChartRef.value || !data) return
  mttrChart = echarts.init(mttrChartRef.value)
  mttrChart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}: {c}小时' },
    xAxis: { type: 'category', data: Object.keys(data) },
    yAxis: { type: 'value', name: '小时' },
    series: [{
      type: 'bar',
      data: Object.values(data),
      itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] }
    }]
  })
}

function handleResize() {
  trendChart?.resize()
  typeChart?.resize()
  statusChart?.resize()
  mttrChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  typeChart?.dispose()
  statusChart?.dispose()
  mttrChart?.dispose()
})
</script>

<style scoped>
.analytics-page { display: flex; flex-direction: column; gap: 0; }
.kpi-row { margin-bottom: 0; }
.kpi-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.kpi-value { font-size: 28px; font-weight: 700; margin-bottom: 4px; }
.kpi-title { font-size: 14px; color: #303133; font-weight: 500; }
.kpi-desc { font-size: 12px; color: #909399; margin-top: 2px; }
</style>
