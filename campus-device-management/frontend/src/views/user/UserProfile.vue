<template>
  <div class="user-profile">
    <el-row :gutter="20">
      <!-- Profile Card -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="80" :icon="UserFilled" class="user-avatar" />
            <h3>{{ profile.realName || profile.username || '用户' }}</h3>
            <el-tag :type="roleTagType" size="small">{{ roleLabel }}</el-tag>
          </div>
          <el-descriptions :column="1" border class="profile-desc">
            <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ profile.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ profile.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ profile.department || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ profile.position || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ profile.lastLoginTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- Edit Form -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <span class="card-title">编辑个人信息</span>
          </template>
          <el-form ref="formRef" :model="editForm" :rules="editRules" label-width="80px">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入电话" />
            </el-form-item>
            <el-form-item label="部门" prop="department">
              <el-input v-model="editForm.department" placeholder="请输入部门" />
            </el-form-item>
            <el-form-item label="职位" prop="position">
              <el-input v-model="editForm.position" placeholder="请输入职位" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- Login Records -->
        <el-card class="records-card">
          <template #header>
            <span class="card-title">最近登录记录</span>
          </template>
          <el-table :data="loginRecords" stripe size="small">
            <el-table-column prop="loginTime" label="登录时间" width="180" />
            <el-table-column prop="loginIp" label="登录IP" width="140" />
            <el-table-column prop="loginDevice" label="设备" min-width="200" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile, getLoginRecords } from '../../api/auth'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const formRef = ref(null)
const saving = ref(false)
const profile = ref({})
const loginRecords = ref([])

const editForm = reactive({
  realName: '',
  email: '',
  phone: '',
  department: '',
  position: ''
})

const editRules = {
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
}

const roleLabel = computed(() => {
  const role = profile.value.role || authStore.user?.role || ''
  if (role.includes('ADMIN')) return '管理员'
  if (role.includes('MAINTAINER')) return '维护员'
  return '普通用户'
})

const roleTagType = computed(() => {
  const role = profile.value.role || authStore.user?.role || ''
  if (role.includes('ADMIN')) return 'danger'
  if (role.includes('MAINTAINER')) return 'warning'
  return ''
})

async function loadProfile() {
  try {
    const res = await getUserProfile()
    const data = res?.data || res
    profile.value = data
    editForm.realName = data.realName || ''
    editForm.email = data.email || ''
    editForm.phone = data.phone || ''
    editForm.department = data.department || ''
    editForm.position = data.position || ''
  } catch {
    // Use local auth store data as fallback
    const user = authStore.user || {}
    profile.value = { username: user.username, realName: user.realName }
  }
}

async function loadLoginRecords() {
  try {
    const res = await getLoginRecords(10)
    loginRecords.value = res?.data || res || []
  } catch {
    loginRecords.value = []
  }
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await updateUserProfile(editForm)
    ElMessage.success('个人信息已更新')
    loadProfile()
  } catch {
    ElMessage.error('更新失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

function resetForm() {
  editForm.realName = profile.value.realName || ''
  editForm.email = profile.value.email || ''
  editForm.phone = profile.value.phone || ''
  editForm.department = profile.value.department || ''
  editForm.position = profile.value.position || ''
}

onMounted(() => {
  loadProfile()
  loadLoginRecords()
})
</script>

<style scoped>
.user-profile {
  max-width: 1200px;
}

.profile-card {
  text-align: center;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.avatar-section h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.user-avatar {
  background: linear-gradient(135deg, #1a237e, #0d47a1);
}

.profile-desc {
  margin-top: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.records-card {
  margin-top: 20px;
}
</style>
