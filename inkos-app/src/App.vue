<template>
  <view class="app-root">
    <page-meta :page-style="themeStyle" />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { onLaunch } from '@dcloudio/uni-app'
import { useThemeStore } from '@/stores/theme'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const themeStore = useThemeStore()
const userStore = useUserStore()
const appStore = useAppStore()

const themeStyle = computed(() => {
  const c = themeStore.colors
  return `--text-primary:${c.textPrimary};--text-secondary:${c.textSecondary};--text-tertiary:${c.textTertiary};--text-inverse:${c.textInverse};--bg-primary:${c.bgPrimary};--bg-secondary:${c.bgSecondary};--bg-tertiary:${c.bgTertiary};--bg-card:${c.bgCard};--accent-primary:${c.accentPrimary};--accent-secondary:${c.accentSecondary};--accent-warm:${c.accentWarm};--accent-danger:${c.accentDanger};--accent-success:${c.accentSuccess};`
})

onLaunch(() => {
  themeStore.init()
  userStore.loadLocal()
  appStore.checkNetwork()
  if (!userStore.isLoggedIn) {
    uni.reLaunch({ url: '/pages/auth/login' })
  }
})

const adminPages = ['pages/admin/index', 'pages/admin/users', 'pages/admin/whitelist']

function isAdminPage(path: string): boolean {
  return adminPages.some(p => path.includes(p))
}

try {
  const app = getApp()
  const originalRoute = uni.navigateTo
  uni.navigateTo = function(opt: any) {
    if (opt.url && isAdminPage(opt.url)) {
      if (!userStore.isLoggedIn) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        uni.reLaunch({ url: '/pages/auth/login' })
        return
      }
      if (!userStore.isAdmin) {
        uni.showToast({ title: '需要管理员权限', icon: 'none' })
        uni.switchTab({ url: '/pages/index/index' })
        return
      }
    }
    originalRoute.call(uni, opt)
  }

  const originalSwitchTab = uni.switchTab
  uni.switchTab = function(opt: any) {
    if (opt.url && isAdminPage(opt.url)) {
      if (!userStore.isLoggedIn) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        uni.reLaunch({ url: '/pages/auth/login' })
        return
      }
      if (!userStore.isAdmin) {
        uni.showToast({ title: '需要管理员权限', icon: 'none' })
        uni.switchTab({ url: '/pages/index/index' })
        return
      }
    }
    originalSwitchTab.call(uni, opt)
  }

  const originalRedirectTo = uni.redirectTo
  uni.redirectTo = function(opt: any) {
    if (opt.url && isAdminPage(opt.url)) {
      if (!userStore.isLoggedIn) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        uni.reLaunch({ url: '/pages/auth/login' })
        return
      }
      if (!userStore.isAdmin) {
        uni.showToast({ title: '需要管理员权限', icon: 'none' })
        uni.switchTab({ url: '/pages/index/index' })
        return
      }
    }
    originalRedirectTo.call(uni, opt)
  }
} catch {}
</script>

<style lang="scss">
page {
  background-color: $bg-primary;
  color: $text-primary;
}
</style>
