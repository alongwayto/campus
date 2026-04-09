<template>
  <div class="dashboard">
    <!-- Stat Cards -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <div class="stat-card" :style="{ borderTopColor: card.color }">
          <div class="stat-icon" :style="{ background: card.color + '1a', color: card.color }">
            <el-icon :size="28"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-title">{{ card.title }}</div>
          </div>
          <div class="stat-trend" :style="{ color: card.color }">
            <el-icon><TrendCharts /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="10">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">设备状态分布</span>
          </template>
          <div ref="pieRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">近12个月故障趋势</span>
          </template>
          <div ref="lineRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Recent Faults Table -->
    <el-card class="recent-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">最近故障记录</span>
          <el-button text type="primary" @click="$router.push('/faults')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentFaults" stripe>
        <el-table-column prop="title" label="故障标题" min-width="160" />
        <el-table-column prop="deviceName" label="设备名称" width="140" />
        <el-table-column prop="severity" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="severityType(row.severity)" size="small">{{ row.severity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="上报时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const pieRef = ref(null)
const lineRef = ref(null)
let pieChart = null
let lineChart = null

const statCards = ref([
  { title: '设备总数', value: 328, color: '#409EFF', icon: 'Monitor' },
  { title: '在线设备', value: 256, color: '#67C23A', icon: 'CircleCheck' },
  { title: '故障设备', value: 18, color: '#F56C6C', icon: 'CircleClose' },
  { title: '待处理故障', value: 7, color: '#E6A23C', icon: 'Warning' }
])

const recentFaults = ref([
  { title: '教学楼3F投影仪无法启动', deviceName: '爱普生投影仪', severity: '高', status: '处理中', reportTime: '2024-01-15 09:32' },
  { title: '图书馆打印机卡纸', deviceName: 'HP激光打印机', severity: '中', status: '已解决', reportTime: '2024-01-15 08:15' },
  { title: '实验室A网络交换机异常', deviceName: 'Cisco交换机', severity: '高', status: '待处理', reportTime: '2024-01-14 17:45' },
  { title: '行政楼空调不制冷', deviceName: '格力空调', severity: '中', status: '已派单', reportTime: '2024-01-14 14:20' },
  { title: '体育馆音响设备杂音', deviceName: 'BOSE音响', severity: '低', status: '待处理', reportTime: '2024-01-14 10:05' }
])

function severityType(s) {
  return s === '高' ? 'danger' : s === '中' ? 'warning' : 'info'
}

function statusType(s) {
  if (s === '已解决') return 'success'
  if (s === '处理中' || s === '已派单') return 'warning'
  return 'info'
}

function initPieChart() {
  pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, left: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '68%'],
      center: ['50%', '45%'],
      data: [
        { value: 256, name: '在线', itemStyle: { color: '#67C23A' } },
        { value: 54, name: '离线', itemStyle: { color: '#909399' } },
        { value: 18, name: '故障', itemStyle: { color: '#F56C6C' } }
      ],
      label: { formatter: '{b}\n{d}%' }
    }]
  })
}

function initLineChart() {
  lineChart = echarts.init(lineRef.value)
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['故障数量', '已解决'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value' },
    series: [
      {
        name: '故障数量',
        type: 'line',
        smooth: true,
        data: [12, 8, 15, 10, 18, 22, 16, 14, 20, 17, 13, 11],
        itemStyle: { color: '#F56C6C' },
        areaStyle: { color: 'rgba(245, 108, 108, 0.1)' }
      },
      {
        name: '已解决',
        type: 'line',
        smooth: true,
        data: [10, 7, 14, 9, 16, 20, 15, 13, 18, 15, 12, 10],
        itemStyle: { color: '#67C23A' },
        areaStyle: { color: 'rgba(103, 194, 58, 0.1)' }
      }
    ]
  })
}

function handleResize() {
  pieChart?.resize()
  lineChart?.resize()
}

onMounted(() => {
  initPieChart()
  initLineChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-top: 3px solid transparent;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a237e;
  line-height: 1.2;
}

.stat-title {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.stat-trend {
  font-size: 20px;
  opacity: 0.5;
}

.chart-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.chart-container {
  height: 280px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.recent-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
</style>
