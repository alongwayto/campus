<template>
  <div class="dashboard">
    <!-- Stat Cards -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <div class="stat-card" :style="{ borderTopColor: card.color, cursor: 'pointer' }" @click="$router.push(card.link)">
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

    <!-- Recent Faults & AI Alerts -->
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">最近故障记录</span>
              <el-button text type="primary" @click="$router.push('/faults')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentFaults" stripe size="small">
            <el-table-column prop="title" label="故障标题" min-width="160" show-overflow-tooltip />
            <el-table-column prop="deviceName" label="设备名称" width="140" show-overflow-tooltip />
            <el-table-column prop="severity" label="严重程度" width="80">
              <template #default="{ row }">
                <el-tag :type="severityType(row.severity)" size="small">{{ row.severity }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reportTime" label="上报时间" width="140" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="alert-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon style="color: #e6a23c; margin-right: 4px"><WarningFilled /></el-icon>
                AI 智能预警
              </span>
            </div>
          </template>
          <div class="alert-list">
            <div v-for="alert in anomalyAlerts" :key="alert.id" class="alert-item" :class="'alert-' + alert.level">
              <div class="alert-header">
                <el-tag :type="alert.level === 'high' ? 'danger' : alert.level === 'medium' ? 'warning' : 'info'" size="small">
                  {{ alert.level === 'high' ? '高' : alert.level === 'medium' ? '中' : '低' }}
                </el-tag>
                <span class="alert-type">{{ alert.type }}</span>
              </div>
              <div class="alert-content">{{ alert.message }}</div>
              <div class="alert-device">{{ alert.device }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
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
  { title: '设备总数', value: 25, color: '#409EFF', icon: 'Monitor', link: '/devices' },
  { title: '在线设备', value: 15, color: '#67C23A', icon: 'CircleCheck', link: '/devices' },
  { title: '故障设备', value: 6, color: '#F56C6C', icon: 'CircleClose', link: '/faults' },
  { title: '待处理故障', value: 7, color: '#E6A23C', icon: 'Warning', link: '/faults' }
])

const recentFaults = ref([
  { title: '复印机扫描功能故障', deviceName: '教务处复印机', severity: '中', status: '处理中', reportTime: '2026-04-14 08:00' },
  { title: '服务器内存报错', deviceName: '数据中心服务器-01', severity: '高', status: '待处理', reportTime: '2026-04-13 15:00' },
  { title: '查询终端屏幕闪烁', deviceName: '图书馆查询终端-01', severity: '中', status: '处理中', reportTime: '2026-04-13 08:30' },
  { title: '智能讲台话筒无声', deviceName: '教学楼A-301智能讲台', severity: '中', status: '已派单', reportTime: '2026-04-12 13:00' },
  { title: '打印机打印模糊', deviceName: '行政办公室激光打印机', severity: '低', status: '待处理', reportTime: '2026-04-12 10:00' },
  { title: '示波器探头损坏', deviceName: '电子实验室示波器-01', severity: '中', status: '处理中', reportTime: '2026-04-12 08:00' }
])

const anomalyAlerts = ref([
  { id: 1, level: 'high', type: '设备故障', message: '6台设备处于故障状态，需要及时处理', device: '图书馆查询终端等' },
  { id: 2, level: 'high', type: '服务器预警', message: '服务器内存ECC纠错频率异常增高', device: '数据中心服务器-01' },
  { id: 3, level: 'medium', type: '设备离线', message: '4台设备处于离线状态，建议排查', device: '宿舍楼B区无线AP等' },
  { id: 4, level: 'medium', type: '保修到期', message: '3台设备保修即将到期', device: '图书馆交换机等' },
  { id: 5, level: 'low', type: '维护提醒', message: '5台设备需要定期维护保养', device: '行政楼中央空调主机等' }
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
        { value: 15, name: '在线', itemStyle: { color: '#67C23A' } },
        { value: 4, name: '离线', itemStyle: { color: '#909399' } },
        { value: 6, name: '故障', itemStyle: { color: '#F56C6C' } }
      ],
      label: { formatter: '{b}\n{d}%' }
    }]
  })
}

function initLineChart() {
  lineChart = echarts.init(lineRef.value)
  const months = ['5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月', '1月', '2月', '3月', '4月']
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
        data: [5, 8, 6, 10, 7, 9, 12, 8, 11, 6, 14, 30],
        itemStyle: { color: '#F56C6C' },
        areaStyle: { color: 'rgba(245, 108, 108, 0.1)' }
      },
      {
        name: '已解决',
        type: 'line',
        smooth: true,
        data: [4, 7, 5, 9, 6, 8, 11, 7, 10, 5, 12, 22],
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
  display: flex;
  align-items: center;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.recent-card,
.alert-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-item {
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid #909399;
  background: #f9f9fb;
}

.alert-high { border-left-color: #f56c6c; background: #fef0f0; }
.alert-medium { border-left-color: #e6a23c; background: #fdf6ec; }
.alert-low { border-left-color: #409eff; background: #ecf5ff; }

.alert-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.alert-type {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.alert-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.alert-device {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
