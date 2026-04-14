import request from './request'

export function getUserProfile(userId) {
  return request.get(`/user/profile/${userId}`)
}

export function updateUserProfile(userId, data) {
  return request.put(`/user/profile/${userId}`, data)
}

export function changePassword(data) {
  return request.post('/user/password', data)
}

export function getLoginRecords(userId, limit = 10) {
  return request.get(`/user/login-records/${userId}`, { params: { limit } })
}
