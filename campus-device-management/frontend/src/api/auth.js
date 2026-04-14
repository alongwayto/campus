import request from './request'

export function login(username, password, captchaCode, sessionId) {
  return request.post('/auth/login', { username, password, captchaCode, sessionId })
}

export function getCaptcha() {
  return request.get('/captcha')
}
