<template>
  <view class="admin-page">
    <PageNav title="用户管理" />

    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索邮箱..."
          @confirm="handleSearch"
          confirm-type="search"
        />
        <text class="search-clear" v-if="keyword" @tap="clearSearch">✕</text>
      </view>
    </view>

    <view class="stats-row">
      <view class="stat-card">
        <text class="num">{{ stats.total }}</text>
        <text class="label">总用户</text>
      </view>
      <view class="stat-card">
        <text class="num">{{ stats.active }}</text>
        <text class="label">活跃</text>
      </view>
      <view class="stat-card">
        <text class="num">{{ stats.admin }}</text>
        <text class="label">管理员</text>
      </view>
      <view class="stat-card">
        <text class="num">{{ stats.disabled }}</text>
        <text class="label">已禁用</text>
      </view>
    </view>

    <view v-if="loading" class="loading-wrap">
      <LoadingSpinner tip="加载用户列表..." />
    </view>

    <view v-else-if="users.length === 0" class="empty-wrap">
      <EmptyState
        icon="👥"
        title="暂无用户"
        :desc="keyword ? '没有匹配的邮箱' : '还没有注册用户'"
      />
    </view>

    <scroll-view v-else class="user-list" scroll-y @scrolltolower="loadMore">
      <view class="user-row" v-for="u in users" :key="u.id">
        <view class="user-info">
          <view class="user-main">
            <text class="user-email">{{ u.email }}</text>
            <text class="user-nickname">{{ u.nickname || '-' }}</text>
          </view>
          <view class="user-meta">
            <text class="tag role" :class="u.role">{{ u.role === 'admin' ? '管理员' : '用户' }}</text>
            <text class="tag status" :class="u.status">{{ u.status === 'active' ? '正常' : '已禁用' }}</text>
            <text class="user-id">ID: {{ u.id }}</text>
          </view>
          <view class="user-times">
            <text class="time-label">注册: {{ formatDate(u.createdAt) }}</text>
            <text class="time-label" v-if="u.lastLoginAt">最后登录: {{ formatDate(u.lastLoginAt) }}</text>
          </view>
        </view>
        <view
          class="action-btn"
          :class="u.status === 'active' ? 'danger' : 'success'"
          @tap="toggleStatus(u)"
        >
          {{ u.status === 'active' ? '禁用' : '启用' }}
        </view>
      </view>

      <view class="load-more" v-if="hasMore">
        <LoadingSpinner size="40" tip="加载更多..." />
      </view>
    </scroll-view>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { api, formatDate, type AdminUser } from '@/api'

const allUsers = ref<AdminUser[]>([])
const keyword = ref('')
const loading = ref(false)
const loadingMore = ref(false)
const pageSize = 20
const currentPage = ref(1)

const filteredUsers = computed(() => {
  if (!keyword.value.trim()) return allUsers.value
  const kw = keyword.value.trim().toLowerCase()
  return allUsers.value.filter(u => u.email.toLowerCase().includes(kw))
})

const users = computed(() => {
  const list = filteredUsers.value
  const end = currentPage.value * pageSize
  return list.slice(0, end)
})

const stats = computed(() => {
  const list = allUsers.value
  return {
    total: list.length,
    active: list.filter(u => u.status === 'active').length,
    admin: list.filter(u => u.role === 'admin').length,
    disabled: list.filter(u => u.status === 'disabled').length,
  }
})

const hasMore = computed(() => {
  return currentPage.value * pageSize < filteredUsers.value.length
})

onShow(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    allUsers.value = await api.admin.listUsers()
    currentPage.value = 1
  } catch (e: any) {
    uni.showToast({ title: e.message || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
}

function clearSearch() {
  keyword.value = ''
  currentPage.value = 1
}

function loadMore() {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  setTimeout(() => {
    currentPage.value++
    loadingMore.value = false
  }, 300)
}

async function toggleStatus(u: AdminUser) {
  const newStatus = u.status === 'active' ? 'disabled' : 'active'
  const action = newStatus === 'disabled' ? '禁用' : '启用'
  uni.showModal({
    title: '确认操作',
    content: `确定要${action}用户 ${u.nickname || u.email} 吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await api.admin.updateUserStatus(u.id, newStatus)
          uni.showToast({ title: `已${action}`, icon: 'success' })
          await loadUsers()
        } catch (e: any) {
          uni.showToast({ title: e.message || '操作失败', icon: 'none' })
        }
      }
    },
  })
}
</script>

<style lang="scss" scoped>
@import './style';

.search-input-wrap {
  display: flex;
  align-items: center;
  background: $bg-card;
  border-radius: $radius-lg;
  padding: 0 $spacing-md;
  height: 72rpx;
  box-shadow: $shadow-sm;
}

.search-icon {
  font-size: 28rpx;
  margin-right: $spacing-sm;
}

.search-input {
  flex: 1;
  font-size: $font-sm;
  color: $text-primary;
  height: 100%;
}

.search-clear {
  font-size: 24rpx;
  color: $text-tertiary;
  padding: 8rpx;
}

.user-list {
  padding: 0 $spacing-md;
  height: calc(100vh - 360rpx);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-main {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

.user-email {
  font-size: $font-base;
  font-weight: 600;
  color: $text-primary;
  flex-shrink: 0;
}

.user-nickname {
  font-size: $font-sm;
  color: $text-tertiary;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  margin-top: $spacing-xs;
}

.user-id {
  font-size: 20rpx;
  color: $text-tertiary;
}

.user-times {
  margin-top: $spacing-xs;
  display: flex;
  gap: $spacing-sm;
  flex-wrap: wrap;
}

.time-label {
  font-size: 20rpx;
  color: $text-tertiary;
}

.tag {
  font-size: 20rpx;
  padding: 2rpx 14rpx;
  border-radius: 20rpx;

  &.role {
    &.admin { background: rgba(99, 102, 241, 0.1); color: $accent-secondary; }
    &.user { background: rgba(59, 130, 246, 0.1); color: $accent-primary; }
  }

  &.status {
    &.active { background: rgba(16, 185, 129, 0.1); color: $accent-success; }
    &.disabled { background: rgba(239, 68, 68, 0.1); color: $accent-danger; }
  }
}

.action-btn {
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
  font-size: $font-xs;
  border: 2rpx solid;
  white-space: nowrap;
  flex-shrink: 0;

  &.danger { border-color: $accent-danger; color: $accent-danger; }
  &.success { border-color: $accent-success; color: $accent-success; }
}

.load-more {
  padding: $spacing-md 0;
}
</style>
