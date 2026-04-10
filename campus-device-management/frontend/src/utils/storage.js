const PREFIX = 'campus_'

export function setItem(key, value) {
  try {
    const data = JSON.stringify({ value, timestamp: Date.now() })
    localStorage.setItem(PREFIX + key, data)
  } catch (e) {
    console.error('Storage setItem error:', e)
  }
}

export function getItem(key, defaultValue = null) {
  try {
    const data = localStorage.getItem(PREFIX + key)
    if (!data) return defaultValue
    const parsed = JSON.parse(data)
    return parsed.value
  } catch (e) {
    return defaultValue
  }
}

export function removeItem(key) {
  localStorage.removeItem(PREFIX + key)
}

export function clearAll() {
  Object.keys(localStorage)
    .filter(key => key.startsWith(PREFIX))
    .forEach(key => localStorage.removeItem(key))
}

// Token management
export function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token) {
  localStorage.setItem('token', token)
}

export function removeToken() {
  localStorage.removeItem('token')
}
