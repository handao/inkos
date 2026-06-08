<template>
  <view class="profile">
    <view class="profile-header">
      <image
        class="avatar"
        :src="userStore.profile?.avatarUrl || '/static/default-avatar.svg'"
        mode="aspectFill"
      />
      <view class="profile-info">
        <text class="name">{{ userStore.profile?.nickname || '未登录' }}</text>
        <text class="bio">{{ userStore.profile?.email || '' }}</text>
        <text class="role-badge" v-if="userStore.isAdmin">管理员</text>
      </view>
    </view>

    <view class="stats-grid">
      <view class="stats-item">
        <text class="stats-value">{{ libraryStore.books.length }}</text>
        <text class="stats-label">作品</text>
      </view>
      <view class="stats-item">
        <text class="stats-value">{{ totalChapters }}</text>
        <text class="stats-label">章节</text>
      </view>
    </view>

    <view class="menu-group">
      <view class="menu-item" @tap="goLibrary">
        <text class="menu-icon">📚</text>
        <text class="menu-label">我的文库</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goCreate">
        <text class="menu-icon">✏️</text>
        <text class="menu-label">新建作品</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goWorkspace">
        <text class="menu-icon">🤖</text>
        <text class="menu-label">AI 写作工作台</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-group">
      <view class="menu-item" @tap="goSettings">
        <text class="menu-icon">⚙️</text>
        <text class="menu-label">设置</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goServices">
        <text class="menu-icon">🔌</text>
        <text class="menu-label">LLM 服务配置</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item">
        <text class="menu-icon">🎨</text>
        <text class="menu-label">主题外观</text>
        <view class="theme-toggle" @tap="themeStore.toggle">
          <text>{{ themeStore.isDark ? '🌙 深色' : '☀️ 浅色' }}</text>
        </view>
      </view>
    </view>

    <view class="menu-group" v-if="userStore.isAdmin">
      <view class="menu-item" @tap="goAdmin">
        <text class="menu-icon">⚡</text>
        <text class="menu-label">管理中心</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-group">
      <view class="menu-item" @tap="showAbout">
        <text class="menu-icon">ℹ️</text>
        <text class="menu-label">关于 InkOS</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="logout-section">
      <view class="logout-btn" @tap="handleLogout" v-if="userStore.isLoggedIn">
        退出登录
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { useLibraryStore } from '@/stores/library'

const userStore = useUserStore()
const themeStore = useThemeStore()
const libraryStore = useLibraryStore()

const totalChapters = computed(() =>
  libraryStore.books.reduce((s, b) => s + b.chaptersWritten, 0)
)

onShow(() => {
  libraryStore.fetchBooks()
})

function goLibrary() { uni.switchTab({ url: '/pages/library/index' }) }
function goCreate() { uni.navigateTo({ url: '/pages/create/create' }) }
function goWorkspace() { uni.navigateTo({ url: '/pages/workspace/index' }) }
function goSettings() { uni.navigateTo({ url: '/pages/settings/index' }) }
function goServices() { uni.navigateTo({ url: '/pages/settings/services' }) }
function goAdmin() { uni.navigateTo({ url: '/pages/admin/index' }) }

function showAbout() {
  uni.showModal({
    title: '关于 InkOS',
    content: 'InkOS v1.0.0\nAI Novel Writing Studio\n基于多智能体管线的小说创作平台',
  })
}

function handleLogout() {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) userStore.logout()
    },
  })
}
</script>

<style lang="scss" scoped>
.profile { min-height: 100vh; background: $bg-primary; }
.profile-header {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-xl $spacing-lg;
  background: $bg-card;
}
.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: $bg-tertiary;
  flex-shrink: 0;
}
.profile-info { flex: 1; min-width: 0; }
.name { font-size: $font-lg; font-weight: 700; color: $text-primary; display: block; }
.bio { font-size: $font-sm; color: $text-tertiary; margin-top: 4rpx; display: block; }
.role-badge {
  display: inline-block;
  font-size: 20rpx;
  color: $accent-secondary;
  background: rgba(99, 102, 241, 0.1);
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
  margin-top: $spacing-xs;
}
.stats-grid {
  display: flex;
  background: $bg-card;
  margin-top: $spacing-sm;
  padding: $spacing-md 0;
}
.stats-item {
  flex: 1;
  text-align: center;
  border-right: 1rpx solid $border-light;
  &:last-child { border: none; }
}
.stats-value { font-size: $font-xl; font-weight: 700; color: $text-primary; display: block; }
.stats-label { font-size: $font-xs; color: $text-tertiary; margin-top: 4rpx; display: block; }
.menu-group {
  margin-top: $spacing-md;
  background: $bg-card;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: $spacing-md $spacing-lg;
  border-bottom: 1rpx solid $border-light;
  &:last-child { border: none; }
}
.menu-icon { font-size: 32rpx; margin-right: $spacing-md; }
.menu-label { flex: 1; font-size: $font-base; color: $text-primary; }
.menu-arrow { font-size: $font-lg; color: $text-tertiary; }
.theme-toggle {
  padding: 4rpx 20rpx;
  border-radius: 30rpx;
  background: $bg-tertiary;
  font-size: $font-xs;
  color: $text-secondary;
}
.logout-section {
  margin-top: $spacing-xl;
  padding: $spacing-md $spacing-lg;
}
.logout-btn {
  text-align: center;
  padding: $spacing-sm;
  border-radius: $radius-md;
  border: 2rpx solid $accent-danger;
  color: $accent-danger;
  font-size: $font-base;
}
</style>
