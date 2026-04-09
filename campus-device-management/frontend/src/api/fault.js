import request from './request'

export function getFaults(params) {
  return request.get('/faults', { params })
}

export function addFault(data) {
  return request.post('/faults', data)
}

export function updateFault(id, data) {
  return request.put(`/faults/${id}`, data)
}

export function assignFault(id, assigneeId) {
  return request.put(`/faults/${id}/assign`, { assigneeId })
}

export function resolveFault(id, notes) {
  return request.put(`/faults/${id}/resolve`, { notes })
}

export function deleteFault(id) {
  return request.delete(`/faults/${id}`)
}
