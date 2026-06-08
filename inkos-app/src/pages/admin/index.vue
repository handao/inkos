<template>
  <view class="admin-page">
    <PageNav title="管理中心" />

    <view class="stats-row">
      <view class="stat-card">
        <text class="num">{{ stats.totalUsers }}</text>
        <text class="label">总用户</text>
      </view>
      <view class="stat-card">
        <text class="num">{{ stats.activeUsers }}</text>
        <text class="label">活跃用户</text>
      </view>
      <view class="stat-card">
        <text class="num">{{ stats.totalBooks }}</text>
        <text class="label">总作品</text>
      </view>
      <view class="stat-card">
        <text class="num">{{ stats.totalChapters }}</text>
        <text class="label">总章节</text>
      </view>
    </view>

    <view class="menu-group">
      <view class="menu-item" @tap="goUsers">
        <text class="menu-icon">👥</text>
        <view class="menu-content">
          <text class="menu-title">用户管理</text>
          <text class="menu-desc">查看和管理所有注册用户</text>
        </view>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goWhitelist">
        <text class="menu-icon">📋</text>
        <view class="menu-content">
          <text class="menu-title">白名单管理</text>
          <text class="menu-desc">管理允许注册的邮箱地址</text>
        </view>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">最近活动</text>
      <view class="activity-placeholder">
        <text class="activity-icon">📊</text>
        <text class="activity-desc">活动日志功能即将上线</text>
      </view>
    </view>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { api } from '@/api'

const stats = ref({
  totalUsers: 0,
  activeUsers: 0,
  totalBooks: 0,
  totalChapters: 0,
})

onShow(async () => {
  try {
    const users = await api.admin.listUsers()
    const active = users.filter(u => u.status === 'active')
    stats.value = {
      totalUsers: users.length,
      activeUsers: active.length,
      totalBooks: 0,
      totalChapters: 0,
    }
  } catch {}
})

function goUsers() {
  uni.navigateTo({ url: '/pages/admin/users' })
}

function goWhitelist() {
  uni.navigateTo({ url: '/pages/admin/whitelist' })
}
</script>

<style lang="scss" scoped>
@import './style';

.menu-group {
  margin: $spacing-md;
  background: $bg-card;
  border-radius: $radius-md;
  overflow: hidden;
  box-shadow: $shadow-sm;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: $spacing-md $spacing-lg;
  border-bottom: 1rpx solid $border-light;

  &:last-child {
    border-bottom: none;
  }
}

.menu-icon {
  font-size: 40rpx;
  margin-right: $spacing-md;
}

.menu-content {
  flex: 1;
  min-width: 0;
}

.menu-title {
  font-size: $font-base;
  font-weight: 600;
  color: $text-primary;
  display: block;
}

.menu-desc {
  font-size: $font-xs;
  color: $text-tertiary;
  margin-top: 4rpx;
  display: block;
}

.menu-arrow {
  font-size: $font-lg;
  color: $text-tertiary;
  margin-left: $spacing-sm;
}

.section {
  margin: $spacing-md;
}

.section-title {
  font-size: $font-md;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: $spacing-sm;
}

.activity-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200rpx;
  background: $bg-card;
  border-radius: $radius-md;
  border: 2rpx dashed $border-light;
  gap: $spacing-sm;
}

.activity-icon {
  font-size: 48rpx;
}

.activity-desc {
  font-size: $font-sm;
  color: $text-tertiary;
}
</style>
