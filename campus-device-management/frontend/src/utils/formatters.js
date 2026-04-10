import dayjs from 'dayjs'
import { DEVICE_STATUS_LABELS, FAULT_STATUS_LABELS, SEVERITY_LABELS } from './constants'

export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '-'
  return dayjs(date).format(format)
}

export function formatDateShort(date) {
  if (!date) return '-'
  return dayjs(date).format('MM-DD HH:mm')
}

export function formatDeviceStatus(status) {
  return DEVICE_STATUS_LABELS[status] ?? '未知'
}

export function formatFaultStatus(status) {
  return FAULT_STATUS_LABELS[status] ?? '未知'
}

export function formatSeverity(severity) {
  return SEVERITY_LABELS[severity] ?? '未知'
}

export function formatFileSize(bytes) {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export function formatDuration(minutes) {
  if (!minutes) return '-'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}

export function formatPercent(value, decimals = 1) {
  if (value === null || value === undefined) return '-'
  return (value * 100).toFixed(decimals) + '%'
}
