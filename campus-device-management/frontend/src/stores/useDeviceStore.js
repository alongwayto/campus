import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getDevices } from '../api/device'

export const useDeviceStore = defineStore('device', () => {
  const devices = ref([])
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  const onlineCount = computed(() => devices.value.filter(d => d.status === 1).length)
  const faultCount = computed(() => devices.value.filter(d => d.status === 2).length)

  async function fetchDevices(params = {}) {
    loading.value = true
    try {
      const res = await getDevices({ current: currentPage.value, size: pageSize.value, ...params })
      devices.value = res.list || res.records || []
      total.value = res.total || 0
    } catch (e) {
      console.error('Failed to fetch devices:', e)
    } finally {
      loading.value = false
    }
  }

  function reset() {
    devices.value = []
    total.value = 0
    currentPage.value = 1
  }

  return { devices, loading, total, currentPage, pageSize, onlineCount, faultCount, fetchDevices, reset }
})
