<template>
  <div class="monitor">
    <!-- Summary -->
    <el-row :gutter="16" class="summary-row">
      <el-col :span="6" v-for="item in summary" :key="item.label">
        <div class="summary-card" :style="{ borderColor: item.color }">
          <div class="summary-dot" :class="item.dotClass"></div>
          <div class="summary-count" :style="{ color: item.color }">{{ item.count }}</div>
          <div class="summary-label">{{ item.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- Toolbar -->
    <div class="toolbar">
      <span class="last-update">上次刷新：{{ lastUpdateTime }}</span>
      <el-button :icon="Refresh" @click="refreshDevices" :loading="loading">立即刷新</el-button>
      <el-select v-model="filterStatus" placeholder="筛选状态" clearable style="width: 130px" @change="applyFilter">
        <el-option label="全部" value="" />
        <el-option label="在线" value="online" />
        <el-option label="离线" value="offline" />
        <el-option label="故障" value="fault" />
      </el-select>
    </div>

    <!-- Device Cards Grid -->
    <div v-loading="loading" class="cards-grid">
      <div
        v-for="device in filteredDevices"
        :key="device.id"
        class="device-card"
        :class="'device-card--' + device.status"
      >
        <div class="card-header">
          <div class="status-indicator" :class="'indicator--' + device.status"></div>
          <span class="device-name">{{ device.name }}</span>
        </div>
        <div class="card-body">
          <div class="info-row">
            <el-icon><Location /></el-icon>
            <span>{{ device.location }}</span>
          </div>
          <div class="info-row">
            <el-icon><Monitor /></el-icon>
            <span>{{ device.type }}</span>
          </div>
          <div class="info-row">
            <el-icon><Clock /></el-icon>
            <span>{{ device.lastUpdate }}</span>
          </div>
        </div>
        <div class="card-footer">
          <el-tag :type="statusTagType(device.status)" size="small" round>
            {{ statusLabel(device.status) }}
          </el-tag>
        </div>
      </div>

      <el-empty v-if="!loading && filteredDevices.length === 0" description="暂无设备数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Refresh, Location, Clock } from '@element-plus/icons-vue'

const loading = ref(false)
const filterStatus = ref('')
const lastUpdateTime = ref('')
let timer = null

const allDevices = ref([
  { id: 1, name: '爱普生投影仪-A301', type: '投影仪', location: '教学楼A-301', status: 'online', lastUpdate: '2024-01-15 09:50' },
  { id: 2, name: 'HP激光打印机-B201', type: '打印机', location: '图书馆B-201', status: 'offline', lastUpdate: '2024-01-15 08:30' },
  { id: 3, name: 'Cisco交换机-C102', type: '交换机', location: '机房C-102', status: 'online', lastUpdate: '2024-01-15 09:55' },
  { id: 4, name: '格力空调-D405', type: '空调', location: '行政楼D-405', status: 'fault', lastUpdate: '2024-01-15 07:20' },
  { id: 5, name: 'Dell服务器-E001', type: '服务器', location: '数据中心E-001', status: 'online', lastUpdate: '2024-01-15 09:58' },
  { id: 6, name: '海康摄像头-F203', type: '摄像头', location: '操场F-203', status: 'online', lastUpdate: '2024-01-15 09:57' },
  { id: 7, name: '磁卡门禁-G101', type: '门禁', location: '实验楼G-101', status: 'online', lastUpdate: '2024-01-15 09:55' },
  { id: 8, name: 'BOSE音响-H001', type: '音响', location: '体育馆H-001', status: 'fault', lastUpdate: '2024-01-14 18:00' },
  { id: 9, name: '联想笔记本-I201', type: '电脑', location: '会议室I-201', status: 'online', lastUpdate: '2024-01-15 09:30' },
  { id: 10, name: '施乐复印机-J302', type: '打印机', location: '办公区J-302', status: 'offline', lastUpdate: '2024-01-15 06:00' },
  { id: 11, name: 'TP-Link路由器-K001', type: '网络', location: '宿舍楼K-001', status: 'online', lastUpdate: '2024-01-15 09:59' },
  { id: 12, name: '智能白板-L101', type: '教学设备', location: '创客空间L-101', status: 'online', lastUpdate: '2024-01-15 08:00' }
])

const filteredDevices = computed(() => {
  if (!filterStatus.value) return allDevices.value
  return allDevices.value.filter(d => d.status === filterStatus.value)
})

const summary = computed(() => [
  { label: '设备总数', count: allDevices.value.length, color: '#409EFF', dotClass: '' },
  { label: '在线', count: allDevices.value.filter(d => d.status === 'online').length, color: '#67C23A', dotClass: 'pulse-green' },
  { label: '离线', count: allDevices.value.filter(d => d.status === 'offline').length, color: '#909399', dotClass: '' },
  { label: '故障', count: allDevices.value.filter(d => d.status === 'fault').length, color: '#F56C6C', dotClass: 'pulse-red' }
])

function statusTagType(s) {
  return s === 'online' ? 'success' : s === 'offline' ? 'info' : 'danger'
}

function statusLabel(s) {
  return s === 'online' ? '在线' : s === 'offline' ? '离线' : '故障'
}

function applyFilter() {}

function updateTime() {
  const now = new Date()
  lastUpdateTime.value = now.toLocaleString('zh-CN')
}

async function refreshDevices() {
  loading.value = true
  updateTime()
  await new Promise(r => setTimeout(r, 600))
  loading.value = false
}

onMounted(() => {
  updateTime()
  timer = setInterval(refreshDevices, 30000)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.monitor {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-left: 4px solid;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.summary-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #d0d3d8;
}

.pulse-green {
  background: #67C23A;
  animation: pulse 1.5s infinite;
}

.pulse-red {
  background: #F56C6C;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.3); }
}

.summary-count {
  font-size: 26px;
  font-weight: 700;
  line-height: 1;
}

.summary-label {
  font-size: 13px;
  color: #909399;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.last-update {
  font-size: 13px;
  color: #909399;
  flex: 1;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.device-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
  border-top: 3px solid #ebeef5;
}

.device-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.device-card--online { border-top-color: #67C23A; }
.device-card--offline { border-top-color: #909399; }
.device-card--fault { border-top-color: #F56C6C; }

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.indicator--online {
  background: #67C23A;
  animation: pulse 2s infinite;
}
.indicator--offline { background: #909399; }
.indicator--fault {
  background: #F56C6C;
  animation: pulse 1s infinite;
}

.device-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #606266;
}

.info-row .el-icon {
  font-size: 13px;
  color: #909399;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
