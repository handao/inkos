<template>
  <view class="admin-page">
    <PageNav title="白名单管理">
      <template #right>
        <text class="add-btn" @tap="showAdd = true">+ 添加</text>
      </template>
    </PageNav>

    <view class="stats-row">
      <view class="stat-card">
        <text class="num">{{ whitelist.length }}</text>
        <text class="label">白名单数</text>
      </view>
    </view>

    <view v-if="loading" class="loading-wrap">
      <LoadingSpinner tip="加载白名单..." />
    </view>

    <view v-else-if="whitelist.length === 0" class="empty-wrap">
      <EmptyState
        icon="📋"
        title="白名单为空"
        desc="添加允许注册的邮箱地址"
        actionText="添加邮箱"
        @action="showAdd = true"
      />
    </view>

    <scroll-view v-else class="whitelist-list" scroll-y>
      <view class="whitelist-row" v-for="item in whitelist" :key="item.email">
        <view class="whitelist-info">
          <text class="email-address">{{ item.email }}</text>
          <text class="email-note" v-if="item.note">{{ item.note }}</text>
          <text class="email-date">添加于 {{ formatDate(item.createdAt) }}</text>
        </view>
        <view class="delete-btn" @tap="handleDelete(item)">删除</view>
      </view>
    </scroll-view>

    <view class="modal-mask" v-if="showAdd" @tap="showAdd = false">
      <view class="modal-content" @tap.stop>
        <text class="modal-title">添加白名单</text>
        <view class="form-group">
          <text class="label">邮箱</text>
          <input
            class="input"
            v-model="newEmail"
            type="email"
            placeholder="输入邮箱地址"
            @confirm="handleAdd"
          />
        </view>
        <view class="form-group">
          <text class="label">备注（可选）</text>
          <input class="input" v-model="newNote" placeholder="如：合作作者" />
        </view>
        <view class="modal-actions">
          <view class="modal-btn cancel" @tap="showAdd = false">取消</view>
          <view class="modal-btn confirm" @tap="handleAdd" :class="{ loading: adding }">
            {{ adding ? '添加中...' : '添加' }}
          </view>
        </view>
      </view>
    </view>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { api, formatDate, type AllowedEmail } from '@/api'

const whitelist = ref<AllowedEmail[]>([])
const loading = ref(false)
const showAdd = ref(false)
const adding = ref(false)
const newEmail = ref('')
const newNote = ref('')

onShow(() => {
  loadWhitelist()
})

async function loadWhitelist() {
  loading.value = true
  try {
    whitelist.value = await api.admin.listAllowedEmails()
  } catch (e: any) {
    uni.showToast({ title: e.message || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function validateEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

async function handleAdd() {
  const email = newEmail.value.trim()
  if (!email) {
    uni.showToast({ title: '请输入邮箱', icon: 'none' })
    return
  }
  if (!validateEmail(email)) {
    uni.showToast({ title: '邮箱格式不正确', icon: 'none' })
    return
  }
  if (whitelist.value.some(e => e.email === email)) {
    uni.showToast({ title: '该邮箱已在白名单中', icon: 'none' })
    return
  }
  adding.value = true
  try {
    await api.admin.addAllowedEmail({
      email,
      note: newNote.value.trim() || undefined,
    })
    uni.showToast({ title: '添加成功', icon: 'success' })
    showAdd.value = false
    newEmail.value = ''
    newNote.value = ''
    await loadWhitelist()
  } catch (e: any) {
    uni.showToast({ title: e.message || '添加失败', icon: 'none' })
  } finally {
    adding.value = false
  }
}

function handleDelete(item: AllowedEmail) {
  uni.showModal({
    title: '确认删除',
    content: `确定要从白名单中移除 ${item.email} 吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await api.admin.deleteAllowedEmail(item.email)
          uni.showToast({ title: '已移除', icon: 'success' })
          await loadWhitelist()
        } catch (e: any) {
          uni.showToast({ title: e.message || '删除失败', icon: 'none' })
        }
      }
    },
  })
}
</script>

<style lang="scss" scoped>
@import './style';

.add-btn {
  font-size: $font-sm;
  color: $accent-primary;
  font-weight: 500;
}

.whitelist-list {
  padding: 0 $spacing-md;
  height: calc(100vh - 240rpx);
}

.whitelist-info {
  flex: 1;
  min-width: 0;
}

.email-address {
  font-size: $font-base;
  font-weight: 500;
  color: $text-primary;
  display: block;
}

.email-note {
  font-size: $font-sm;
  color: $text-secondary;
  margin-top: 4rpx;
  display: block;
}

.email-date {
  font-size: 20rpx;
  color: $text-tertiary;
  margin-top: 4rpx;
  display: block;
}

.delete-btn {
  font-size: $font-sm;
  color: $accent-danger;
  padding: 8rpx 16rpx;
  flex-shrink: 0;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 600rpx;
  background: $bg-secondary;
  border-radius: $radius-lg;
  padding: $spacing-xl;
}

.modal-title {
  font-size: $font-lg;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: $spacing-md;
}

.form-group {
  margin-bottom: $spacing-md;
}

.label {
  font-size: $font-sm;
  color: $text-secondary;
  display: block;
  margin-bottom: $spacing-xs;
  font-weight: 500;
}

.input {
  width: 100%;
  height: 72rpx;
  background: $bg-primary;
  border-radius: $radius-sm;
  padding: 0 $spacing-sm;
  font-size: $font-sm;
  color: $text-primary;
  border: 2rpx solid $border-light;
}

.modal-actions {
  display: flex;
  gap: $spacing-md;
  margin-top: $spacing-lg;
}

.modal-btn {
  flex: 1;
  text-align: center;
  padding: $spacing-sm;
  border-radius: $radius-md;
  font-size: $font-base;
  font-weight: 500;

  &.cancel {
    background: $bg-tertiary;
    color: $text-secondary;
  }

  &.confirm {
    background: $accent-primary;
    color: #fff;

    &.loading {
      opacity: 0.7;
    }
  }
}
</style>
