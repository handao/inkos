import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { lightTheme, darkTheme, getSystemTheme, type Theme, type ThemeColors } from '@/theme'

export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref<Theme>(lightTheme)
  const storedMode = ref<'light' | 'dark' | 'auto'>('auto')

  const colors = computed<ThemeColors>(() => currentTheme.value.colors)
  const isDark = computed(() => currentTheme.value.isDark)

  function applyTheme(mode: 'light' | 'dark') {
    currentTheme.value = mode === 'dark' ? darkTheme : lightTheme
    if (uni && uni.setNavigationBarColor) {
      uni.setNavigationBarColor({
        frontColor: mode === 'dark' ? '#ffffff' : '#000000',
        backgroundColor: mode === 'dark' ? '#1a1a2e' : '#ffffff',
      })
    }
    if (uni && uni.setTabBarStyle) {
      uni.setTabBarStyle({
        color: mode === 'dark' ? '#8e8ea0' : '#999999',
        selectedColor: mode === 'dark' ? '#60a5fa' : '#3b82f6',
        backgroundColor: mode === 'dark' ? '#1a1a2e' : '#ffffff',
      })
    }
  }

  function setMode(mode: 'light' | 'dark' | 'auto') {
    storedMode.value = mode
    if (mode === 'auto') {
      applyTheme(getSystemTheme())
    } else {
      applyTheme(mode)
    }
    uni.setStorageSync('theme-mode', mode)
  }

  function init() {
    const saved = uni.getStorageSync('theme-mode') as 'light' | 'dark' | 'auto' | null
    setMode(saved || 'auto')
  }

  function toggle() {
    setMode(isDark.value ? 'light' : 'dark')
  }

  return { currentTheme, colors, isDark, storedMode, setMode, applyTheme, init, toggle }
})
