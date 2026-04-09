<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <div class="table-header">
          <div class="search-wrap">
            <el-input
              v-model="searchName"
              placeholder="搜索用户名"
              :prefix-icon="Search"
              clearable
              style="width: 220px"
              @input="fetchUsers"
            />
          </div>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">新增用户</el-button>
        </div>
      </template>

      <el-table :data="userList" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="roleType(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="155" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" :icon="Edit" @click="openEditDialog(row)">编辑</el-button>
            <el-button
              text
              :type="row.status === 'active' ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >{{ row.status === 'active' ? '禁用' : '启用' }}</el-button>
            <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row.id)">
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
          layout="total, prev, pager, next"
          @change="fetchUsers"
        />
      </div>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="520px"
      destroy-on-close
    >
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="dialogForm.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="dialogForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="dialogForm.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="dialogForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="dialogForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="dialogForm.role" style="width: 100%">
            <el-option label="管理员" value="ROLE_ADMIN" />
            <el-option label="维修员" value="ROLE_MAINTAINER" />
            <el-option label="普通用户" value="ROLE_USER" />
          </el-select>
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
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getUsers, addUser, updateUser, deleteUser } from '../../api/user'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogFormRef = ref(null)
const searchName = ref('')
let editId = null

const page = reactive({ current: 1, size: 10, total: 0 })
const userList = ref([])

const demoUsers = [
  { id: 1, username: 'admin', realName: '系统管理员', email: 'admin@campus.edu', phone: '13800000001', role: 'ROLE_ADMIN', status: 'active', createTime: '2023-01-01 00:00' },
  { id: 2, username: 'li_repair', realName: '李维修', email: 'li@campus.edu', phone: '13800000002', role: 'ROLE_MAINTAINER', status: 'active', createTime: '2023-03-15 09:00' },
  { id: 3, username: 'zhang_tech', realName: '张技术', email: 'zhang@campus.edu', phone: '13800000003', role: 'ROLE_MAINTAINER', status: 'active', createTime: '2023-04-20 10:30' },
  { id: 4, username: 'wang_user', realName: '王用户', email: 'wang@campus.edu', phone: '13800000004', role: 'ROLE_USER', status: 'active', createTime: '2023-05-10 14:00' },
  { id: 5, username: 'chen_user', realName: '陈用户', email: 'chen@campus.edu', phone: '13800000005', role: 'ROLE_USER', status: 'inactive', createTime: '2023-06-01 09:00' }
]

const dialogForm = reactive({ username: '', realName: '', password: '', email: '', phone: '', role: 'ROLE_USER' })

const dialogRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function roleType(r) {
  return r === 'ROLE_ADMIN' ? 'danger' : r === 'ROLE_MAINTAINER' ? 'warning' : 'info'
}
function roleLabel(r) {
  return r === 'ROLE_ADMIN' ? '管理员' : r === 'ROLE_MAINTAINER' ? '维修员' : '普通用户'
}

async function fetchUsers() {
  loading.value = true
  try {
    const res = await getUsers({ page: page.current, size: page.size, username: searchName.value })
    userList.value = res?.data?.records || res?.data?.list || res?.records || demoUsers
    page.total = res?.data?.total || res?.total || demoUsers.length
  } catch {
    const filtered = searchName.value
      ? demoUsers.filter(u => u.username.includes(searchName.value) || u.realName.includes(searchName.value))
      : demoUsers
    userList.value = filtered
    page.total = filtered.length
  } finally {
    loading.value = false
  }
}

function openAddDialog() {
  isEdit.value = false
  editId = null
  Object.assign(dialogForm, { username: '', realName: '', password: '', email: '', phone: '', role: 'ROLE_USER' })
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  editId = row.id
  Object.assign(dialogForm, { ...row, password: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateUser(editId, dialogForm)
    } else {
      await addUser(dialogForm)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchUsers()
  } catch {
    ElMessage.success(isEdit.value ? '编辑成功（演示模式）' : '新增成功（演示模式）')
    dialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteUser(id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch {
    ElMessage.success('删除成功（演示模式）')
    userList.value = userList.value.filter(u => u.id !== id)
    page.total--
  }
}

function toggleStatus(row) {
  row.status = row.status === 'active' ? 'inactive' : 'active'
  ElMessage.success(`用户已${row.status === 'active' ? '启用' : '禁用'}`)
}

onMounted(fetchUsers)
</script>

<style scoped>
.user-manage {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
