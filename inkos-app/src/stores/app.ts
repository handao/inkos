import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const loading = ref(false)
  const networkAvailable = ref(true)
  const hasNewVersion = ref(false)

  function setLoading(v: boolean) {
    loading.value = v
  }

  function checkNetwork() {
    uni.getNetworkType({
      success: (res) => {
        networkAvailable.value = res.networkType !== 'none'
      },
    })
  }

  function showToast(title: string, icon: 'success' | 'error' | 'none' = 'none') {
    uni.showToast({ title, icon, duration: 2000 })
  }

  function showModal(title: string, content: string): Promise<boolean> {
    return new Promise((resolve) => {
      uni.showModal({
        title,
        content,
        success: (res) => resolve(res.confirm),
      })
    })
  }

  return { loading, networkAvailable, hasNewVersion, setLoading, checkNetwork, showToast, showModal }
})
