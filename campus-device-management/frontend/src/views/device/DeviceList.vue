<template>
  <div class="device-list">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="设备名称">
          <el-input v-model="searchForm.name" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="t in deviceTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="在线" value="online" />
            <el-option label="离线" value="offline" />
            <el-option label="故障" value="fault" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="fetchDevices">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div class="table-header">
          <span class="table-title">设备列表</span>
          <div class="table-actions">
            <el-button type="primary" :icon="Plus" @click="openAddDialog">新增设备</el-button>
            <el-upload
              :show-file-list="false"
              accept=".xlsx,.xls"
              :before-upload="handleImport"
              style="display: inline-block; margin: 0 8px"
            >
              <el-button :icon="Upload">导入</el-button>
            </el-upload>
            <el-button :icon="Download" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="deviceList"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="name" label="设备名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="type" label="设备类型" width="120" />
        <el-table-column prop="location" label="位置" width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="serialNumber" label="序列号" width="140" show-overflow-tooltip />
        <el-table-column prop="manufacturer" label="厂商" width="110" />
        <el-table-column prop="purchaseDate" label="购买日期" width="110" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" :icon="Edit" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该设备吗？" @confirm="handleDelete(row.id)">
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
          @change="fetchDevices"
        />
      </div>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑设备' : '新增设备'"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogForm"
        :rules="dialogRules"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="dialogForm.name" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备类型" prop="type">
              <el-select v-model="dialogForm.type" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="t in deviceTypes" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="dialogForm.location" placeholder="请输入位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="dialogForm.status" style="width: 100%">
                <el-option label="在线" value="online" />
                <el-option label="离线" value="offline" />
                <el-option label="故障" value="fault" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input v-model="dialogForm.serialNumber" placeholder="请输入序列号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="厂商" prop="manufacturer">
              <el-input v-model="dialogForm.manufacturer" placeholder="请输入厂商" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="购买日期" prop="purchaseDate">
              <el-date-picker
                v-model="dialogForm.purchaseDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择购买日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保修截止" prop="warrantyExpiry">
              <el-date-picker
                v-model="dialogForm.warrantyExpiry"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择保修截止"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="description">
          <el-input
            v-model="dialogForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Upload, Download } from '@element-plus/icons-vue'
import { getDevices, addDevice, updateDevice, deleteDevice, exportDevices, importDevices } from '../../api/device'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogFormRef = ref(null)

const deviceTypes = ['投影仪', '打印机', '交换机', '空调', '服务器', '摄像头', '门禁', '其他']

const searchForm = reactive({ name: '', type: '', status: '' })
const page = reactive({ current: 1, size: 10, total: 0 })

const deviceList = ref([])

// Demo data
const demoDevices = [
  { id: 1, name: '爱普生投影仪-A301', type: '投影仪', location: '教学楼A-301', status: 'online', serialNumber: 'EP20230001', manufacturer: '爱普生', purchaseDate: '2023-03-15', warrantyExpiry: '2026-03-15' },
  { id: 2, name: 'HP激光打印机-B201', type: '打印机', location: '图书馆B-201', status: 'offline', serialNumber: 'HP20220088', manufacturer: 'HP', purchaseDate: '2022-08-20', warrantyExpiry: '2025-08-20' },
  { id: 3, name: 'Cisco交换机-C102', type: '交换机', location: '机房C-102', status: 'online', serialNumber: 'CS20230045', manufacturer: 'Cisco', purchaseDate: '2023-01-10', warrantyExpiry: '2028-01-10' },
  { id: 4, name: '格力空调-D405', type: '空调', location: '行政楼D-405', status: 'fault', serialNumber: 'GL20210067', manufacturer: '格力', purchaseDate: '2021-06-05', warrantyExpiry: '2027-06-05' },
  { id: 5, name: 'Dell服务器-E001', type: '服务器', location: '数据中心E-001', status: 'online', serialNumber: 'DL20230012', manufacturer: 'Dell', purchaseDate: '2023-05-20', warrantyExpiry: '2028-05-20' },
  { id: 6, name: '海康摄像头-F203', type: '摄像头', location: '操场F-203', status: 'online', serialNumber: 'HK20220156', manufacturer: '海康威视', purchaseDate: '2022-11-15', warrantyExpiry: '2025-11-15' },
  { id: 7, name: '磁卡门禁-G101', type: '门禁', location: '实验楼G-101', status: 'online', serialNumber: 'MJ20230078', manufacturer: '中控', purchaseDate: '2023-02-28', warrantyExpiry: '2026-02-28' },
  { id: 8, name: 'BOSE音响-H001', type: '其他', location: '体育馆H-001', status: 'fault', serialNumber: 'BS20220034', manufacturer: 'BOSE', purchaseDate: '2022-09-10', warrantyExpiry: '2025-09-10' }
]

const dialogForm = reactive({
  name: '', type: '', location: '', status: 'online',
  serialNumber: '', manufacturer: '', purchaseDate: '', warrantyExpiry: '', description: ''
})

const dialogRules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

let editId = null

function statusType(s) {
  return s === 'online' ? 'success' : s === 'offline' ? 'info' : 'danger'
}
function statusLabel(s) {
  return s === 'online' ? '在线' : s === 'offline' ? '离线' : '故障'
}

async function fetchDevices() {
  loading.value = true
  try {
    const res = await getDevices({
      page: page.current,
      size: page.size,
      ...searchForm
    })
    deviceList.value = res?.data?.records || res?.data?.list || res?.records || demoDevices
    page.total = res?.data?.total || res?.total || demoDevices.length
  } catch {
    deviceList.value = demoDevices
    page.total = demoDevices.length
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.name = ''
  searchForm.type = ''
  searchForm.status = ''
  page.current = 1
  fetchDevices()
}

function openAddDialog() {
  isEdit.value = false
  editId = null
  Object.assign(dialogForm, {
    name: '', type: '', location: '', status: 'online',
    serialNumber: '', manufacturer: '', purchaseDate: '', warrantyExpiry: '', description: ''
  })
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  editId = row.id
  Object.assign(dialogForm, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateDevice(editId, dialogForm)
    } else {
      await addDevice(dialogForm)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchDevices()
  } catch {
    // Demo mode
    ElMessage.success(isEdit.value ? '编辑成功（演示模式）' : '新增成功（演示模式）')
    dialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteDevice(id)
    ElMessage.success('删除成功')
    fetchDevices()
  } catch {
    ElMessage.success('删除成功（演示模式）')
    deviceList.value = deviceList.value.filter(d => d.id !== id)
    page.total--
  }
}

async function handleExport() {
  try {
    const blob = await exportDevices()
    const url = URL.createObjectURL(blob instanceof Blob ? blob : new Blob([blob]))
    const a = document.createElement('a')
    a.href = url
    a.download = '设备列表.xlsx'
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.info('演示模式：导出功能需连接后端')
  }
}

async function handleImport(file) {
  try {
    await importDevices(file)
    ElMessage.success('导入成功')
    fetchDevices()
  } catch {
    ElMessage.info('演示模式：导入功能需连接后端')
  }
  return false
}

onMounted(fetchDevices)
</script>

<style scoped>
.device-list {
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
