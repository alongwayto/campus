export function validateRequired(rule, value, callback) {
  if (!value || (typeof value === 'string' && !value.trim())) {
    callback(new Error(rule.message || '此项为必填项'))
  } else {
    callback()
  }
}

export function validateEmail(rule, value, callback) {
  if (!value) {
    callback()
    return
  }
  const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!reg.test(value)) {
    callback(new Error('邮箱格式不正确'))
  } else {
    callback()
  }
}

export function validatePhone(rule, value, callback) {
  if (!value) {
    callback()
    return
  }
  const reg = /^1[3-9]\d{9}$/
  if (!reg.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

export function validatePassword(rule, value, callback) {
  if (!value) {
    callback(new Error('密码不能为空'))
    return
  }
  if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    callback()
  }
}

// Common validation rules
export const rules = {
  required: (message = '此项为必填项') => [
    { required: true, message, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ]
}
