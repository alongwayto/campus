<template>
  <div class="statistics">
    <el-row :gutter="20">
      <!-- Device Status Pie Chart -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">设备状态分布</span>
          </template>
          <div ref="pieRef" class="chart"></div>
        </el-card>
      </el-col>

      <!-- Fault Type Bar Chart -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">故障类型统计</span>
          </template>
          <div ref="faultBarRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- Device Registration Trend Line Chart -->
      <el-col :span="14">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">设备注册趋势（近12个月）</span>
          </template>
          <div ref="trendLineRef" class="chart"></div>
        </el-card>
      </el-col>

      <!-- Maintenance Cost Bar Chart -->
      <el-col :span="10">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">维修费用统计（近6个月）</span>
          </template>
          <div ref="costBarRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const pieRef = ref(null)
const faultBarRef = ref(null)
const trendLineRef = ref(null)
const costBarRef = ref(null)

let charts = []

function initPieChart() {
  const chart = echarts.init(pieRef.value)
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}台 ({d}%)' },
    legend: { orient: 'vertical', right: 20, top: 'center' },
    series: [{
      type: 'pie',
      radius: ['38%', '65%'],
      center: ['40%', '50%'],
      data: [
        { value: 256, name: '在线设备', itemStyle: { color: '#67C23A' } },
        { value: 54, name: '离线设备', itemStyle: { color: '#909399' } },
        { value: 18, name: '故障设备', itemStyle: { color: '#F56C6C' } }
      ],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
      label: { formatter: '{b}\n{d}%', fontSize: 12 }
    }]
  })
  charts.push(chart)
}

function initFaultBarChart() {
  const chart = echarts.init(faultBarRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['低', '中', '高', '紧急'],
      axisLabel: { fontSize: 12 }
    },
    yAxis: { type: 'value', name: '故障数量' },
    series: [{
      name: '故障数量',
      type: 'bar',
      barWidth: '50%',
      data: [
        { value: 15, itemStyle: { color: '#67C23A' } },
        { value: 28, itemStyle: { color: '#E6A23C' } },
        { value: 22, itemStyle: { color: '#F56C6C' } },
        { value: 8, itemStyle: { color: '#b71c1c' } }
      ],
      label: { show: true, position: 'top' }
    }]
  })
  charts.push(chart)
}

function initTrendLineChart() {
  const chart = echarts.init(trendLineRef.value)
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增设备', '累计设备'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: months },
    yAxis: [
      { type: 'value', name: '新增数量', position: 'left' },
      { type: 'value', name: '累计数量', position: 'right' }
    ],
    series: [
      {
        name: '新增设备',
        type: 'bar',
        data: [12, 8, 15, 20, 18, 25, 22, 16, 19, 23, 14, 11],
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '累计设备',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: [200, 208, 223, 243, 261, 286, 308, 324, 343, 366, 380, 391],
        itemStyle: { color: '#67C23A' },
        areaStyle: { color: 'rgba(103, 194, 58, 0.1)' }
      }
    ]
  })
  charts.push(chart)
}

function initCostBarChart() {
  const chart = echarts.init(costBarRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: '{b}: ¥{c}' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['8月', '9月', '10月', '11月', '12月', '1月']
    },
    yAxis: { type: 'value', name: '费用（元）', axisLabel: { formatter: '¥{value}' } },
    series: [{
      name: '维修费用',
      type: 'bar',
      data: [3200, 2800, 4500, 3800, 5200, 4100],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#1a237e' }
        ])
      },
      label: { show: true, position: 'top', formatter: '¥{c}' }
    }]
  })
  charts.push(chart)
}

function handleResize() {
  charts.forEach(c => c.resize())
}

onMounted(() => {
  initPieChart()
  initFaultBarChart()
  initTrendLineChart()
  initCostBarChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c.dispose())
  charts = []
})
</script>

<style scoped>
.statistics {
  display: flex;
  flex-direction: column;
}

.chart-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart {
  height: 300px;
}
</style>
