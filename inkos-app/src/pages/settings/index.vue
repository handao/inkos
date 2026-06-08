<template>
  <view class="settings">
    <PageNav title="设置" />

    <view class="section">
      <view class="section-header">
        <text class="section-title">账户</text>
      </view>
      <view class="menu-item">
        <text class="menu-label">邮箱</text>
        <text class="menu-value">{{ userStore.profile?.email || '-' }}</text>
      </view>
      <view class="menu-item">
        <text class="menu-label">昵称</text>
        <text class="menu-value">{{ userStore.profile?.nickname || '-' }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">服务</text>
      </view>
      <view class="menu-item" @tap="goServices">
        <text class="menu-label">LLM 服务配置</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">外观</text>
      </view>
      <view class="menu-item">
        <text class="menu-label">主题</text>
        <view class="theme-toggle" @tap="themeStore.toggle">
          <text>{{ themeStore.isDark ? '🌙 深色' : '☀️ 浅色' }}</text>
        </view>
      </view>
      <view class="menu-item">
        <text class="menu-label">当前模式</text>
        <text class="menu-value">{{ currentMode }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">关于</text>
      </view>
      <view class="menu-item">
        <text class="menu-label">版本</text>
        <text class="menu-value">1.0.0</text>
      </view>
      <view class="menu-item" @tap="showAbout">
        <text class="menu-label">关于 InkOS</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'

const userStore = useUserStore()
const themeStore = useThemeStore()

const currentMode = computed(() => {
  if (themeStore.storedMode === 'auto') return '跟随系统'
  return themeStore.storedMode === 'dark' ? '深色' : '浅色'
})

function goServices() { uni.navigateTo({ url: '/pages/settings/services' }) }

function showAbout() {
  uni.showModal({
    title: '关于 InkOS',
    content: 'InkOS v1.0.0\nAI Novel Writing Studio\n基于多智能体管线的小说创作平台',
  })
}
</script>

<style lang="scss" scoped>
.settings {
  min-height: 100vh;
  background: $bg-primary;
}
.section {
  margin-top: $spacing-md;
  background: $bg-card;
}
.section-header {
  padding: $spacing-sm $spacing-lg;
  border-bottom: 1rpx solid $border-light;
}
.section-title {
  font-size: $font-xs;
  color: $text-tertiary;
  font-weight: 500;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: $spacing-md $spacing-lg;
  border-bottom: 1rpx solid $border-light;
  &:last-child { border: none; }
}
.menu-label { flex: 1; font-size: $font-base; color: $text-primary; }
.menu-value { font-size: $font-sm; color: $text-tertiary; }
.menu-arrow { font-size: $font-lg; color: $text-tertiary; }
.theme-toggle {
  padding: 4rpx 20rpx;
  border-radius: 30rpx;
  background: $bg-tertiary;
  font-size: $font-xs;
  color: $text-secondary;
}
</style>
