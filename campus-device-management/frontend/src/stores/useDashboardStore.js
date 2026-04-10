import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getDashboardData } from '../api/dashboard'

export const useDashboardStore = defineStore('dashboard', () => {
  const data = ref(null)
  const loading = ref(false)
  const lastUpdated = ref(null)

  async function fetchDashboardData() {
    loading.value = true
    try {
      data.value = await getDashboardData()
      lastUpdated.value = new Date()
    } catch (e) {
      console.error('Failed to fetch dashboard data:', e)
    } finally {
      loading.value = false
    }
  }

  return { data, loading, lastUpdated, fetchDashboardData }
})
