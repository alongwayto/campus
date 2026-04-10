import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const loading = ref(false)
  const theme = ref('light')
  const language = ref('zh-CN')

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setLoading(val) {
    loading.value = val
  }

  function setTheme(val) {
    theme.value = val
  }

  return { sidebarCollapsed, loading, theme, language, toggleSidebar, setLoading, setTheme }
})
