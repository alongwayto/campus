import request from './request'

export function diagnoseFault(deviceId, description) {
  return request.post('/ai/diagnose', { deviceId, description })
}

export function predictFault(deviceId) {
  return request.get(`/ai/predict/${deviceId}`)
}

export function getHealthScore(deviceId) {
  return request.get(`/ai/health/${deviceId}`)
}

export function recommendMaintainer(deviceId, description) {
  return request.post('/ai/recommend-maintainer', { deviceId, description })
}

export function analyzeText(text) {
  return request.post('/ai/analyze-text', { text })
}

export function getMaintenanceSuggestion(deviceId) {
  return request.get(`/ai/maintenance-suggestion/${deviceId}`)
}

export function predictLifecycle(deviceId) {
  return request.get(`/ai/lifecycle/${deviceId}`)
}

export function detectAnomalies() {
  return request.get('/ai/anomalies')
}

export function recommendSpareParts(deviceId, description) {
  return request.post('/ai/recommend-parts', { deviceId, description })
}
