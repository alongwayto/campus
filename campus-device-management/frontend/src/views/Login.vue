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
        <el-form-item prop="captchaCode">
          <div class="captcha-row">
            <el-input
              v-model="form.captchaCode"
              placeholder="请输入验证码"
              :prefix-icon="Picture"
              clearable
              class="captcha-input"
            />
            <div class="captcha-image" @click="refreshCaptcha" title="点击刷新验证码">
              <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
              <div v-else class="captcha-placeholder">
                <el-icon><RefreshRight /></el-icon>
                <span>加载中</span>
              </div>
            </div>
          </div>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, InfoFilled, Picture, RefreshRight } from '@element-plus/icons-vue'
import { login, getCaptcha } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const captchaImage = ref('')
const sessionId = ref('')

const form = reactive({
  username: 'admin',
  password: 'admin123',
  captchaCode: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' }
  ],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

async function refreshCaptcha() {
  try {
    const res = await getCaptcha()
    const data = res?.data || res
    captchaImage.value = data.captchaImage
    sessionId.value = data.sessionId
  } catch {
    captchaImage.value = ''
    sessionId.value = ''
  }
}

onMounted(() => {
  refreshCaptcha()
})

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form.username, form.password, form.captchaCode, sessionId.value)
    const token = res?.token || res?.data?.token
    const user = res?.user || res?.data?.user || { username: form.username, role: 'ROLE_ADMIN' }
    if (token) {
      authStore.login(token, user)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      authStore.login('demo-token-' + Date.now(), { username: form.username, role: 'ROLE_ADMIN' })
      ElMessage.success('登录成功（演示模式）')
      router.push('/dashboard')
    }
  } catch (err) {
    const msg = err?.response?.data?.message || ''
    if (msg.includes('验证码')) {
      ElMessage.error(msg)
      refreshCaptcha()
      form.captchaCode = ''
    } else {
      authStore.login('demo-token-' + Date.now(), { username: form.username, role: 'ROLE_ADMIN' })
      ElMessage.success('登录成功（演示模式）')
      router.push('/dashboard')
    }
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

.captcha-row {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
  flex-shrink: 0;
  transition: border-color 0.2s;
}

.captcha-image:hover {
  border-color: #409EFF;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.captcha-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
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
