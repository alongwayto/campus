import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUsers } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const users = ref([])
  const loading = ref(false)
  const total = ref(0)

  async function fetchUsers(params = {}) {
    loading.value = true
    try {
      const res = await getUsers(params)
      users.value = res.list || res.records || []
      total.value = res.total || 0
    } catch (e) {
      console.error('Failed to fetch users:', e)
    } finally {
      loading.value = false
    }
  }

  return { users, loading, total, fetchUsers }
})
