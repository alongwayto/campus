import request from './request'

export function login(username, password, captchaCode, sessionId) {
  return request.post('/auth/login', { username, password, captchaCode, sessionId })
}

export function getCaptcha() {
  return request.get('/captcha')
}

export function getUserProfile() {
  return request.get('/user/profile')
}

export function updateUserProfile(data) {
  return request.put('/user/profile', data)
}

export function changePassword(data) {
  return request.post('/user/password', data)
}

export function getLoginRecords(limit = 10) {
  return request.get('/user/login-records', { params: { limit } })
}

export function getAiDiagnosis(faultId) {
  return request.get(`/ai/diagnose/${faultId}`)
}

export function getAiPrediction(deviceId) {
  return request.get(`/ai/predict/${deviceId}`)
}

export function getAiAnomalies() {
  return request.get('/ai/anomalies')
}

export function getDeviceHealthScore(deviceId) {
  return request.get(`/ai/health/${deviceId}`)
}
