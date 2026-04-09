import request from './request'

export function getDevices(params) {
  return request.get('/devices', { params })
}

export function getDevice(id) {
  return request.get(`/devices/${id}`)
}

export function addDevice(data) {
  return request.post('/devices', data)
}

export function updateDevice(id, data) {
  return request.put(`/devices/${id}`, data)
}

export function deleteDevice(id) {
  return request.delete(`/devices/${id}`)
}

export function exportDevices() {
  return request.get('/devices/export', { responseType: 'blob' })
}

export function importDevices(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/devices/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
