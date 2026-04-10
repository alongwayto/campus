import request from './request'

export function diagnoseFault(data) {
  return request.post('/ai/diagnose', data)
}

export function predictDeviceFailure(deviceId) {
  return request.get(`/ai/predict/${deviceId}`)
}

export function batchPredictFailure(deviceIds) {
  return request.post('/ai/predict/batch', deviceIds)
}

export function detectAnomaly(deviceId, metrics) {
  return request.post(`/ai/anomaly/${deviceId}`, metrics)
}

export function recommendMaintainer(faultId) {
  return request.get(`/ai/recommend-maintainer/${faultId}`)
}

export function analyzeText(text) {
  return request.post('/ai/analyze-text', { text })
}

export function smartCategorize(description) {
  return request.post('/ai/categorize', { description })
}
