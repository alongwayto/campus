import request from './request'

export function getAnalyticsData(params) {
  return request.get('/analytics', { params })
}

export function getFaultTrend(months = 12) {
  return request.get('/analytics/fault-trend', { params: { months } })
}

export function getDeviceHealthReport() {
  return request.get('/analytics/device-health')
}

export function getMaintenanceEfficiency() {
  return request.get('/analytics/maintenance-efficiency')
}
