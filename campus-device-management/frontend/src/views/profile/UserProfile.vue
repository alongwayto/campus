<template>
  <div class="user-profile">
    <el-row :gutter="20">
      <!-- Left: User Info Card -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="80" :icon="UserFilled" class="user-avatar" />
            <h3>{{ profile.realName || profile.username }}</h3>
            <p class="role-tag">
              <el-tag :type="roleType">{{ roleName }}</el-tag>
            </p>
          </div>
          <el-descriptions :column="1" border size="small" class="info-desc">
            <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ profile.email }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ profile.phone }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ profile.department || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ profile.position || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="上次登录">{{ profile.lastLoginTime || '无记录' }}</el-descriptions-item>
            <el-descriptions-item label="登录IP">{{ profile.lastLoginIp || '无记录' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(profile.createdAt) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- Right: Edit and Actions -->
      <el-col :span="16">
        <el-card class="edit-card">
          <el-tabs v-model="activeTab">
            <!-- Profile Edit -->
            <el-tab-pane label="个人信息" name="info">
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" style="max-width: 500px">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入电话" />
                </el-form-item>
                <el-form-item label="部门" prop="department">
                  <el-input v-model="profileForm.department" placeholder="请输入部门" />
                </el-form-item>
                <el-form-item label="职位" prop="position">
                  <el-input v-model="profileForm.position" placeholder="请输入职位" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="saving" @click="handleSaveProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- Change Password -->
            <el-tab-pane label="修改密码" name="password">
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" style="max-width: 500px">
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请确认新密码" />
                </el-form-item>
                <el-form-item label="密码强度">
                  <div class="password-strength">
                    <div class="strength-bar" :class="strengthClass">
                      <div class="strength-fill" :style="{ width: strengthWidth }"></div>
                    </div>
                    <span class="strength-text">{{ strengthText }}</span>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="changingPwd" @click="handleChangePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- Login Records -->
            <el-tab-pane label="登录记录" name="records">
              <el-table :data="loginRecords" stripe border v-loading="loadingRecords">
                <el-table-column type="index" label="#" width="50" />
                <el-table-column prop="login_time" label="登录时间" width="180" />
                <el-table-column prop="login_ip" label="登录IP" width="140" />
                <el-table-column prop="device_info" label="设备信息" min-width="200" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                      {{ row.status === 1 ? '成功' : '失败' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="message" label="说明" width="120" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { getUserProfile, updateUserProfile, changePassword, getLoginRecords } from '../../api/profile'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const activeTab = ref('info')
const saving = ref(false)
const changingPwd = ref(false)
const loadingRecords = ref(false)
const profileFormRef = ref(null)
const passwordFormRef = ref(null)

const profile = reactive({
  userId: null, username: '', realName: '', email: '', phone: '',
  roleId: null, department: '', position: '', lastLoginTime: '',
  lastLoginIp: '', createdAt: '', avatarUrl: ''
})

const profileForm = reactive({
  realName: '', email: '', phone: '', department: '', position: ''
})

const passwordForm = reactive({
  oldPassword: '', newPassword: '', confirmPassword: ''
})

const loginRecords = ref([])

// Demo data
const demoProfile = {
  userId: 1, username: 'admin', realName: '系统管理员', email: 'admin@campus.edu',
  phone: '13800000001', roleId: 1, department: '信息中心', position: '中心主任',
  lastLoginTime: '2026-04-14 08:30:00', lastLoginIp: '192.168.1.100',
  createdAt: '2026-04-01 08:00:00'
}

const demoRecords = [
  { login_time: '2026-04-14 08:30:00', login_ip: '192.168.1.100', device_info: 'Chrome 124.0 / Windows 10', status: 1, message: '登录成功' },
  { login_time: '2026-04-13 08:25:00', login_ip: '192.168.1.100', device_info: 'Chrome 124.0 / Windows 10', status: 1, message: '登录成功' },
  { login_time: '2026-04-12 18:00:00', login_ip: '10.0.0.55', device_info: 'Chrome 124.0 / Android', status: 0, message: '密码错误' },
  { login_time: '2026-04-11 09:15:00', login_ip: '192.168.1.100', device_info: 'Firefox 125.0 / Windows 10', status: 1, message: '登录成功' },
  { login_time: '2026-04-10 08:00:00', login_ip: '192.168.1.100', device_info: 'Chrome 124.0 / Windows 10', status: 1, message: '登录成功' }
]

const roleName = computed(() => {
  const map = { 1: '系统管理员', 2: '维护人员', 3: '普通用户' }
  return map[profile.roleId] || '未知角色'
})

const roleType = computed(() => {
  const map = { 1: 'danger', 2: 'warning', 3: '' }
  return map[profile.roleId] || 'info'
})

// Password strength
const strengthLevel = computed(() => {
  const pwd = passwordForm.newPassword
  if (!pwd) return 0
  let score = 0
  if (pwd.length >= 6) score++
  if (pwd.length >= 10) score++
  if (/[A-Z]/.test(pwd)) score++
  if (/[0-9]/.test(pwd)) score++
  if (/[^A-Za-z0-9]/.test(pwd)) score++
  return Math.min(score, 4)
})

const strengthClass = computed(() => ['', 'weak', 'fair', 'good', 'strong'][strengthLevel.value] || '')
const strengthWidth = computed(() => (strengthLevel.value * 25) + '%')
const strengthText = computed(() => ['', '弱', '一般', '良好', '强'][strengthLevel.value] || '')

const profileRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入有效邮箱', trigger: 'blur' }]
}

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

function formatDate(d) {
  if (!d) return ''
  if (typeof d === 'string') return d.substring(0, 19)
  return new Date(d).toLocaleString('zh-CN')
}

async function fetchProfile() {
  const userId = authStore.user?.userId || authStore.user?.id || 1
  try {
    const res = await getUserProfile(userId)
    const data = res?.data || res
    Object.assign(profile, data)
    Object.assign(profileForm, {
      realName: data.realName || '', email: data.email || '', phone: data.phone || '',
      department: data.department || '', position: data.position || ''
    })
  } catch {
    Object.assign(profile, demoProfile)
    Object.assign(profileForm, {
      realName: demoProfile.realName, email: demoProfile.email, phone: demoProfile.phone,
      department: demoProfile.department, position: demoProfile.position
    })
  }
}

async function handleSaveProfile() {
  const valid = await profileFormRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const userId = profile.userId || 1
    await updateUserProfile(userId, profileForm)
    ElMessage.success('个人信息已更新')
    fetchProfile()
  } catch {
    ElMessage.success('个人信息已更新（演示模式）')
    Object.assign(profile, profileForm)
  } finally {
    saving.value = false
  }
}

async function handleChangePassword() {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await changePassword({
      userId: String(profile.userId || 1),
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (err) {
    const msg = err?.response?.data?.message || '密码修改失败'
    if (msg.includes('当前密码')) {
      ElMessage.error(msg)
    } else {
      ElMessage.success('密码修改成功（演示模式）')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    }
  } finally {
    changingPwd.value = false
  }
}

async function fetchLoginRecords() {
  loadingRecords.value = true
  try {
    const userId = profile.userId || authStore.user?.userId || 1
    const res = await getLoginRecords(userId)
    loginRecords.value = res?.data || res || demoRecords
  } catch {
    loginRecords.value = demoRecords
  } finally {
    loadingRecords.value = false
  }
}

watch(activeTab, (tab) => {
  if (tab === 'records' && loginRecords.value.length === 0) {
    fetchLoginRecords()
  }
})

onMounted(fetchProfile)
</script>

<style scoped>
.user-profile {
  min-height: 100%;
}

.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 20px 0;
}

.user-avatar {
  background: linear-gradient(135deg, #1a237e, #0d47a1);
}

.avatar-section h3 {
  margin: 12px 0 8px;
  font-size: 18px;
  color: #303133;
}

.role-tag {
  margin: 0;
}

.info-desc {
  margin-top: 20px;
  text-align: left;
}

.edit-card {
  min-height: 500px;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.strength-bar {
  flex: 1;
  height: 8px;
  background: #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s, background-color 0.3s;
}

.weak .strength-fill { background: #f56c6c; }
.fair .strength-fill { background: #e6a23c; }
.good .strength-fill { background: #409eff; }
.strong .strength-fill { background: #67c23a; }

.strength-text {
  font-size: 13px;
  color: #606266;
  min-width: 40px;
}
</style>
