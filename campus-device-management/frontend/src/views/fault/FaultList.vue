<template>
  <div class="fault-list">
    <!-- Status Filter Tabs -->
    <el-card class="filter-card">
      <el-tabs v-model="activeTab" @tab-change="fetchFaults">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="待处理" name="pending" />
        <el-tab-pane label="处理中" name="processing" />
        <el-tab-pane label="已派单" name="assigned" />
        <el-tab-pane label="已解决" name="resolved" />
      </el-tabs>
    </el-card>

    <el-card>
      <template #header>
        <div class="table-header">
          <span class="table-title">故障工单列表</span>
          <el-button type="primary" :icon="Plus" @click="openReportDialog">上报故障</el-button>
        </div>
      </template>

      <el-table :data="faultList" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="title" label="故障标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="deviceName" label="设备名称" width="140" />
        <el-table-column prop="reporter" label="上报人" width="100" />
        <el-table-column prop="severity" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="severityType(row.severity)" size="small">{{ row.severity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="faultStatusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="处理人" width="100" />
        <el-table-column prop="reportTime" label="上报时间" width="155" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '待处理'"
              text type="primary" size="small" :icon="UserFilled"
              @click="openAssignDialog(row)"
            >派单</el-button>
            <el-button
              v-if="row.status !== '已解决'"
              text type="success" size="small" :icon="CircleCheck"
              @click="openResolveDialog(row)"
            >解决</el-button>
            <el-popconfirm title="确定删除该故障记录吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button text type="danger" size="small" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :total="page.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchFaults"
        />
      </div>
    </el-card>

    <!-- Report Fault Dialog -->
    <el-dialog v-model="reportVisible" title="上报故障" width="560px" destroy-on-close>
      <el-form ref="reportFormRef" :model="reportForm" :rules="reportRules" label-width="90px">
        <el-form-item label="故障标题" prop="title">
          <el-input v-model="reportForm.title" placeholder="请输入故障标题" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="reportForm.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="严重程度" prop="severity">
          <el-select v-model="reportForm.severity" style="width: 100%">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="description">
          <el-input v-model="reportForm.description" type="textarea" :rows="4" placeholder="请详细描述故障情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleReport">提交</el-button>
      </template>
    </el-dialog>

    <!-- Assign Dialog -->
    <el-dialog v-model="assignVisible" title="派单处理" width="420px" destroy-on-close>
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="90px">
        <el-form-item label="处理人" prop="assigneeId">
          <el-select v-model="assignForm.assigneeId" placeholder="请选择处理人" style="width: 100%">
            <el-option v-for="m in maintainers" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="assignForm.priority" style="width: 100%">
            <el-option label="普通" value="normal" />
            <el-option label="加急" value="urgent" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- Resolve Dialog -->
    <el-dialog v-model="resolveVisible" title="解决故障" width="420px" destroy-on-close>
      <el-form ref="resolveFormRef" :model="resolveForm" :rules="resolveRules" label-width="90px">
        <el-form-item label="解决说明" prop="notes">
          <el-input v-model="resolveForm.notes" type="textarea" :rows="4" placeholder="请填写故障解决说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveVisible = false">取消</el-button>
        <el-button type="success" :loading="submitLoading" @click="handleResolve">确认解决</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, UserFilled, CircleCheck } from '@element-plus/icons-vue'
import { getFaults, addFault, assignFault, resolveFault, deleteFault } from '../../api/fault'

const loading = ref(false)
const submitLoading = ref(false)
const activeTab = ref('')
const reportVisible = ref(false)
const assignVisible = ref(false)
const resolveVisible = ref(false)
const reportFormRef = ref(null)
const assignFormRef = ref(null)
const resolveFormRef = ref(null)
let currentFaultId = null

const page = reactive({ current: 1, size: 10, total: 0 })
const faultList = ref([])

const demoFaults = [
  { id: 1, title: '教学楼3F投影仪无法启动', deviceName: '爱普生投影仪', reporter: '张老师', severity: '高', status: '处理中', assignee: '李维修', reportTime: '2024-01-15 09:32' },
  { id: 2, title: '图书馆打印机卡纸', deviceName: 'HP激光打印机', reporter: '王馆长', severity: '中', status: '已解决', assignee: '赵技术', reportTime: '2024-01-15 08:15' },
  { id: 3, title: '实验室A网络交换机异常', deviceName: 'Cisco交换机', reporter: '刘老师', severity: '高', status: '待处理', assignee: '', reportTime: '2024-01-14 17:45' },
  { id: 4, title: '行政楼空调不制冷', deviceName: '格力空调', reporter: '陈主任', severity: '中', status: '已派单', assignee: '孙工程师', reportTime: '2024-01-14 14:20' },
  { id: 5, title: '体育馆音响设备杂音', deviceName: 'BOSE音响', reporter: '周教练', severity: '低', status: '待处理', assignee: '', reportTime: '2024-01-14 10:05' },
  { id: 6, title: '机房服务器风扇噪音过大', deviceName: 'Dell服务器', reporter: '吴管理员', severity: '紧急', status: '处理中', assignee: '郑工程师', reportTime: '2024-01-13 16:30' }
]

const maintainers = [
  { id: 1, name: '李维修' },
  { id: 2, name: '赵技术' },
  { id: 3, name: '孙工程师' },
  { id: 4, name: '郑工程师' }
]

const reportForm = reactive({ title: '', deviceName: '', severity: '中', description: '' })
const reportRules = {
  title: [{ required: true, message: '请输入故障标题', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  severity: [{ required: true, message: '请选择严重程度', trigger: 'change' }],
  description: [{ required: true, message: '请填写故障描述', trigger: 'blur' }]
}

const assignForm = reactive({ assigneeId: '', priority: 'normal' })
const assignRules = {
  assigneeId: [{ required: true, message: '请选择处理人', trigger: 'change' }]
}

const resolveForm = reactive({ notes: '' })
const resolveRules = {
  notes: [{ required: true, message: '请填写解决说明', trigger: 'blur' }]
}

function severityType(s) {
  return s === '高' || s === '紧急' ? 'danger' : s === '中' ? 'warning' : 'info'
}
function faultStatusType(s) {
  if (s === '已解决') return 'success'
  if (s === '处理中' || s === '已派单') return 'warning'
  return 'info'
}

async function fetchFaults() {
  loading.value = true
  try {
    const res = await getFaults({ page: page.current, size: page.size, status: activeTab.value })
    faultList.value = res?.data?.records || res?.data?.list || res?.records || demoFaults
    page.total = res?.data?.total || res?.total || demoFaults.length
  } catch {
    const filtered = activeTab.value
      ? demoFaults.filter(f => statusMatch(f.status, activeTab.value))
      : demoFaults
    faultList.value = filtered
    page.total = filtered.length
  } finally {
    loading.value = false
  }
}

function statusMatch(status, tab) {
  const map = { pending: '待处理', processing: '处理中', assigned: '已派单', resolved: '已解决' }
  return status === map[tab]
}

function openReportDialog() {
  Object.assign(reportForm, { title: '', deviceName: '', severity: '中', description: '' })
  reportVisible.value = true
}

async function handleReport() {
  const valid = await reportFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addFault(reportForm)
    ElMessage.success('故障上报成功')
    reportVisible.value = false
    fetchFaults()
  } catch {
    ElMessage.success('故障上报成功（演示模式）')
    reportVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

function openAssignDialog(row) {
  currentFaultId = row.id
  assignForm.assigneeId = ''
  assignForm.priority = 'normal'
  assignVisible.value = true
}

async function handleAssign() {
  const valid = await assignFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await assignFault(currentFaultId, assignForm.assigneeId)
    ElMessage.success('派单成功')
    assignVisible.value = false
    fetchFaults()
  } catch {
    ElMessage.success('派单成功（演示模式）')
    assignVisible.value = false
    const target = faultList.value.find(f => f.id === currentFaultId)
    if (target) {
      const m = maintainers.find(m => m.id === assignForm.assigneeId)
      target.status = '已派单'
      target.assignee = m ? m.name : '未知'
    }
  } finally {
    submitLoading.value = false
  }
}

function openResolveDialog(row) {
  currentFaultId = row.id
  resolveForm.notes = ''
  resolveVisible.value = true
}

async function handleResolve() {
  const valid = await resolveFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await resolveFault(currentFaultId, resolveForm.notes)
    ElMessage.success('故障已标记为解决')
    resolveVisible.value = false
    fetchFaults()
  } catch {
    ElMessage.success('故障已解决（演示模式）')
    resolveVisible.value = false
    const target = faultList.value.find(f => f.id === currentFaultId)
    if (target) target.status = '已解决'
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteFault(id)
    ElMessage.success('删除成功')
    fetchFaults()
  } catch {
    ElMessage.success('删除成功（演示模式）')
    faultList.value = faultList.value.filter(f => f.id !== id)
    page.total--
  }
}

onMounted(fetchFaults)
</script>

<style scoped>
.fault-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card :deep(.el-card__body) {
  padding-bottom: 0;
}

.filter-card :deep(.el-tabs__nav-wrap::after) {
  display: none;
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
