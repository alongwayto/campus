import request from './request'

export function getDeviceStatus() {
  return request.get('/stats/device-status')
}

export function getFaultTypes() {
  return request.get('/stats/fault-types')
}

export function getDeviceTrend() {
  return request.get('/stats/device-trend')
}

export function getFaultTrend() {
  return request.get('/stats/fault-trend')
}

export function getMaintenanceCost() {
  return request.get('/stats/maintenance-cost')
}
