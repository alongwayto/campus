<template>
  <div class="change-password">
    <el-card class="password-card">
      <template #header>
        <span class="card-title">修改密码</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="password-form">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码" />
          <div class="password-strength">
            <span>密码强度：</span>
            <div class="strength-bar">
              <div :class="['strength-level', strengthClass]" :style="{ width: strengthWidth }"></div>
            </div>
            <span :class="['strength-text', strengthClass]">{{ strengthText }}</span>
          </div>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">确认修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert type="info" :closable="false" class="password-tips">
        <template #title>密码要求</template>
        <ul>
          <li>密码长度不少于6位</li>
          <li>建议包含大小写字母、数字和特殊字符</li>
          <li>修改密码后需要重新登录</li>
        </ul>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { changePassword } from '../../api/auth'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const passwordStrength = computed(() => {
  const pwd = form.newPassword
  if (!pwd) return 0
  let score = 0
  if (pwd.length >= 6) score++
  if (pwd.length >= 10) score++
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) score++
  if (/\d/.test(pwd)) score++
  if (/[^a-zA-Z0-9]/.test(pwd)) score++
  return Math.min(score, 3)
})

const strengthClass = computed(() => ['', 'weak', 'medium', 'strong'][passwordStrength.value] || '')
const strengthWidth = computed(() => ['0%', '33%', '66%', '100%'][passwordStrength.value] || '0%')
const strengthText = computed(() => ['', '弱', '中', '强'][passwordStrength.value] || '')

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await changePassword({ oldPassword: form.oldPassword, newPassword: form.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    authStore.logout()
    router.push('/login')
  } catch {
    ElMessage.error('密码修改失败')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  formRef.value?.resetFields()
}
</script>

<style scoped>
.change-password {
  max-width: 600px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.password-form {
  max-width: 450px;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}

.strength-bar {
  width: 120px;
  height: 6px;
  background: #e4e7ed;
  border-radius: 3px;
  overflow: hidden;
}

.strength-level {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s, background 0.3s;
}

.strength-level.weak { background: #f56c6c; }
.strength-level.medium { background: #e6a23c; }
.strength-level.strong { background: #67c23a; }

.strength-text.weak { color: #f56c6c; }
.strength-text.medium { color: #e6a23c; }
.strength-text.strong { color: #67c23a; }

.password-tips {
  margin-top: 24px;
}

.password-tips ul {
  margin: 4px 0 0 0;
  padding-left: 16px;
}

.password-tips li {
  line-height: 1.8;
}
</style>
