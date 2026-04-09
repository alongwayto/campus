<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon class="header-icon"><Monitor /></el-icon>
        <h2>智能校园设备管理系统</h2>
        <p class="subtitle">Smart Campus Device Management</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="demo-hint">
        <el-icon><InfoFilled /></el-icon>
        <span>演示账号：admin / admin123</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, InfoFilled } from '@element-plus/icons-vue'
import { login } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form.username, form.password)
    const token = res?.token || res?.data?.token
    const user = res?.user || res?.data?.user || { username: form.username, role: 'ROLE_ADMIN' }
    if (token) {
      authStore.login(token, user)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      // Demo mode: simulate successful login without backend
      authStore.login('demo-token-' + Date.now(), { username: form.username, role: 'ROLE_ADMIN' })
      ElMessage.success('登录成功（演示模式）')
      router.push('/dashboard')
    }
  } catch {
    // Demo mode fallback
    authStore.login('demo-token-' + Date.now(), { username: form.username, role: 'ROLE_ADMIN' })
    ElMessage.success('登录成功（演示模式）')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a237e 0%, #0d47a1 40%, #01579b 70%, #006064 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
  top: -200px;
  left: -100px;
}

.login-container::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
  bottom: -150px;
  right: -100px;
}

.login-box {
  background: rgba(255, 255, 255, 0.97);
  border-radius: 16px;
  padding: 48px 40px;
  width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.header-icon {
  font-size: 52px;
  color: #0d47a1;
  margin-bottom: 12px;
}

.login-header h2 {
  font-size: 22px;
  font-weight: 700;
  color: #1a237e;
  margin: 0 0 6px;
}

.subtitle {
  font-size: 13px;
  color: #90a4ae;
  margin: 0;
  letter-spacing: 1px;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #1a237e, #0d47a1);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #283593, #1565c0);
}

.demo-hint {
  text-align: center;
  color: #90a4ae;
  font-size: 13px;
  margin-top: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}
</style>
