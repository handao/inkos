import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api, type UserProfile } from '@/api'

export const useUserStore = defineStore('user', () => {
  const profile = ref<UserProfile | null>(null)
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!accessToken.value)
  const isAdmin = computed(() => profile.value?.role === 'admin')

  function loadLocal() {
    try {
      const at = uni.getStorageSync('accessToken')
      const rt = uni.getStorageSync('refreshToken')
      if (at) accessToken.value = at
      if (rt) refreshToken.value = rt
      const saved = uni.getStorageSync('user-profile')
      if (saved) {
        try { profile.value = JSON.parse(saved) } catch {}
      }
    } catch {}
  }

  function saveTokens(at: string, rt: string) {
    accessToken.value = at
    refreshToken.value = rt
    uni.setStorageSync('accessToken', at)
    uni.setStorageSync('refreshToken', rt)
  }

  function saveProfile(p: UserProfile) {
    profile.value = p
    uni.setStorageSync('user-profile', JSON.stringify(p))
  }

  async function login(email: string, password: string) {
    loading.value = true
    try {
      const res = await api.auth.login({ email, password })
      saveTokens(res.accessToken, res.refreshToken)
      saveProfile(res.user)
      return res
    } finally {
      loading.value = false
    }
  }

  async function register(email: string, password: string, nickname: string, code: string) {
    loading.value = true
    try {
      const res = await api.auth.register({ email, password, nickname, code })
      saveTokens(res.accessToken, res.refreshToken)
      saveProfile(res.user)
      return res
    } finally {
      loading.value = false
    }
  }

  async function sendCode(email: string) {
    return api.auth.sendCode({ email })
  }

  async function fetchProfile() {
    try {
      const p = await api.users.getMe()
      saveProfile(p)
      return p
    } catch {}
  }

  function setProfile(p: UserProfile) {
    saveProfile(p)
  }

  function logout() {
    accessToken.value = null
    refreshToken.value = null
    profile.value = null
    uni.removeStorageSync('accessToken')
    uni.removeStorageSync('refreshToken')
    uni.removeStorageSync('user-profile')
    uni.reLaunch({ url: '/pages/auth/login' })
  }

  return {
    profile, accessToken, refreshToken, loading,
    isLoggedIn, isAdmin,
    loadLocal, login, register, sendCode, fetchProfile, setProfile, logout,
  }
})
