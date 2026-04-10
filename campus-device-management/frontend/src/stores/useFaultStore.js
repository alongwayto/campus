import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getFaults } from '../api/fault'

export const useFaultStore = defineStore('fault', () => {
  const faults = ref([])
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  const pendingCount = computed(() => faults.value.filter(f => f.status === 0).length)
  const processingCount = computed(() => faults.value.filter(f => f.status === 2).length)

  async function fetchFaults(params = {}) {
    loading.value = true
    try {
      const res = await getFaults({ current: currentPage.value, size: pageSize.value, ...params })
      faults.value = res.list || res.records || []
      total.value = res.total || 0
    } catch (e) {
      console.error('Failed to fetch faults:', e)
    } finally {
      loading.value = false
    }
  }

  function reset() {
    faults.value = []
    total.value = 0
    currentPage.value = 1
  }

  return { faults, loading, total, currentPage, pageSize, pendingCount, processingCount, fetchFaults, reset }
})
