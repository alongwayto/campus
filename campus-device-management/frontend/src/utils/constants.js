// 设备状态
export const DEVICE_STATUS = {
  OFFLINE: 0,
  ONLINE: 1,
  FAULT: 2
}

export const DEVICE_STATUS_LABELS = {
  0: '离线',
  1: '在线',
  2: '故障'
}

export const DEVICE_STATUS_TYPES = {
  0: 'info',
  1: 'success',
  2: 'danger'
}

// 故障严重程度
export const SEVERITY = {
  LOW: 1,
  MEDIUM: 2,
  HIGH: 3
}

export const SEVERITY_LABELS = {
  1: '低',
  2: '中',
  3: '高'
}

export const SEVERITY_TYPES = {
  1: 'info',
  2: 'warning',
  3: 'danger'
}

// 故障状态
export const FAULT_STATUS = {
  PENDING: 0,
  ASSIGNED: 1,
  PROCESSING: 2,
  RESOLVED: 3,
  CLOSED: 4
}

export const FAULT_STATUS_LABELS = {
  0: '待处理',
  1: '已指派',
  2: '处理中',
  3: '已解决',
  4: '已关闭'
}

export const FAULT_STATUS_TYPES = {
  0: 'danger',
  1: 'warning',
  2: 'primary',
  3: 'success',
  4: 'info'
}

// AI风险等级
export const RISK_LEVEL_TYPES = {
  '高风险': 'danger',
  '中风险': 'warning',
  '低风险': 'success'
}

// 角色
export const ROLES = {
  ADMIN: 'ROLE_ADMIN',
  MAINTAINER: 'ROLE_MAINTAINER',
  USER: 'ROLE_USER'
}

export const ROLE_LABELS = {
  ROLE_ADMIN: '管理员',
  ROLE_MAINTAINER: '维护员',
  ROLE_USER: '普通用户'
}
