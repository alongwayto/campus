<template>
  <div class="log-list">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="t in operationTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="fetchLogs">查询</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div class="table-header">
          <span class="table-title">操作日志</span>
          <el-tag type="info">共 {{ page.total }} 条记录</el-tag>
        </div>
      </template>

      <el-table :data="logList" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="username" label="操作用户" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="110" />
        <el-table-column prop="operationType" label="操作类型" width="110">
          <template #default="{ row }">
            <el-tag :type="opTypeTagType(row.operationType)" size="small">{{ row.operationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="功能模块" width="110" />
        <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="result" label="操作结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === '成功' ? 'success' : 'danger'" size="small">{{ row.result }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operateTime" label="操作时间" width="160" />
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :total="page.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchLogs"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getLogs } from '../../api/log'

const loading = ref(false)
const page = reactive({ current: 1, size: 10, total: 0 })
const logList = ref([])

const operationTypes = [
  { label: '登录', value: '登录' },
  { label: '新增', value: '新增' },
  { label: '修改', value: '修改' },
  { label: '删除', value: '删除' },
  { label: '查询', value: '查询' },
  { label: '导入', value: '导入' },
  { label: '导出', value: '导出' }
]

const searchForm = reactive({ username: '', operationType: '', dateRange: null })

const demoLogs = [
  { id: 1, username: 'admin', realName: '系统管理员', operationType: '登录', module: '用户认证', description: '用户登录系统', ip: '192.168.1.100', result: '成功', operateTime: '2024-01-15 09:00:01' },
  { id: 2, username: 'admin', realName: '系统管理员', operationType: '新增', module: '设备管理', description: '新增设备：爱普生投影仪-A302', ip: '192.168.1.100', result: '成功', operateTime: '2024-01-15 09:15:32' },
  { id: 3, username: 'li_repair', realName: '李维修', operationType: '修改', module: '故障管理', description: '更新故障状态：教学楼3F投影仪故障 → 处理中', ip: '192.168.1.105', result: '成功', operateTime: '2024-01-15 09:35:18' },
  { id: 4, username: 'admin', realName: '系统管理员', operationType: '删除', module: '设备管理', description: '删除设备：旧投影仪-A101', ip: '192.168.1.100', result: '成功', operateTime: '2024-01-15 10:02:45' },
  { id: 5, username: 'zhang_tech', realName: '张技术', operationType: '查询', module: '设备管理', description: '查询设备列表', ip: '192.168.1.108', result: '成功', operateTime: '2024-01-15 10:20:00' },
  { id: 6, username: 'wang_user', realName: '王用户', operationType: '新增', module: '故障管理', description: '上报故障：实验室A网络交换机异常', ip: '192.168.1.120', result: '成功', operateTime: '2024-01-15 10:45:12' },
  { id: 7, username: 'admin', realName: '系统管理员', operationType: '导出', module: '设备管理', description: '导出设备列表 Excel 文件', ip: '192.168.1.100', result: '成功', operateTime: '2024-01-15 11:00:33' },
  { id: 8, username: 'admin', realName: '系统管理员', operationType: '导入', module: '设备管理', description: '导入设备数据：25条记录', ip: '192.168.1.100', result: '成功', operateTime: '2024-01-15 11:05:18' },
  { id: 9, username: 'li_repair', realName: '李维修', operationType: '修改', module: '故障管理', description: '解决故障：图书馆打印机卡纸', ip: '192.168.1.105', result: '成功', operateTime: '2024-01-15 11:30:55' },
  { id: 10, username: 'chen_user', realName: '陈用户', operationType: '登录', module: '用户认证', description: '用户登录失败：密码错误', ip: '192.168.2.50', result: '失败', operateTime: '2024-01-15 13:15:02' }
]

function opTypeTagType(t) {
  if (t === '登录') return 'info'
  if (t === '新增') return 'success'
  if (t === '修改') return 'warning'
  if (t === '删除') return 'danger'
  return ''
}

async function fetchLogs() {
  loading.value = true
  try {
    const params = {
      page: page.current,
      size: page.size,
      username: searchForm.username,
      operationType: searchForm.operationType
    }
    if (searchForm.dateRange) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await getLogs(params)
    logList.value = res?.data?.records || res?.data?.list || res?.records || demoLogs
    page.total = res?.data?.total || res?.total || demoLogs.length
  } catch {
    let filtered = [...demoLogs]
    if (searchForm.username) {
      filtered = filtered.filter(l => l.username.includes(searchForm.username) || l.realName.includes(searchForm.username))
    }
    if (searchForm.operationType) {
      filtered = filtered.filter(l => l.operationType === searchForm.operationType)
    }
    logList.value = filtered
    page.total = filtered.length
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.username = ''
  searchForm.operationType = ''
  searchForm.dateRange = null
  page.current = 1
  fetchLogs()
}

onMounted(fetchLogs)
</script>

<style scoped>
.log-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card :deep(.el-card__body) {
  padding-bottom: 0;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
