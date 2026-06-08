<template>
  <view class="login-page">
    <view class="header">
      <text class="logo">✍️</text>
      <text class="title">InkOS</text>
      <text class="subtitle">AI 小说创作平台</text>
    </view>

    <view class="form">
      <view class="form-group">
        <text class="label">邮箱</text>
        <input
          class="input"
          v-model="email"
          type="email"
          placeholder="请输入邮箱地址"
          :disabled="loading"
        />
      </view>

      <view class="form-group">
        <text class="label">密码</text>
        <input
          class="input"
          v-model="password"
          type="password"
          password
          placeholder="请输入密码"
          :disabled="loading"
        />
      </view>

      <view class="error-text" v-if="error">{{ error }}</view>

      <view class="login-btn" :class="{ disabled: !canLogin }" @tap="handleLogin">
        {{ loading ? '登录中...' : '登录' }}
      </view>

      <view class="links">
        <text class="link" @tap="goRegister">没有账号？立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const canLogin = computed(() => email.value.trim().length > 0 && password.value.length > 0)

async function handleLogin() {
  if (!canLogin.value || loading.value) return
  loading.value = true
  error.value = ''
  try {
    await userStore.login(email.value.trim(), password.value)
    uni.switchTab({ url: '/pages/index/index' })
  } catch (e: any) {
    error.value = e.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}

function goRegister() {
  uni.navigateTo({ url: '/pages/auth/register' })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, $accent-primary 0%, $accent-secondary 100%);
  display: flex;
  flex-direction: column;
}
.header {
  padding: 160rpx $spacing-lg 80rpx;
  text-align: center;
  color: #fff;
}
.logo {
  font-size: 80rpx;
  display: block;
  margin-bottom: $spacing-md;
}
.title {
  font-size: 48rpx;
  font-weight: 700;
  display: block;
}
.subtitle {
  font-size: $font-sm;
  opacity: 0.8;
  margin-top: $spacing-xs;
  display: block;
}
.form {
  flex: 1;
  background: $bg-secondary;
  border-radius: $radius-xl $radius-xl 0 0;
  padding: $spacing-xl $spacing-lg;
}
.form-group {
  margin-bottom: $spacing-lg;
}
.label {
  font-size: $font-sm;
  color: $text-secondary;
  display: block;
  margin-bottom: $spacing-sm;
  font-weight: 500;
}
.input {
  width: 100%;
  height: 88rpx;
  background: $bg-primary;
  border-radius: $radius-md;
  padding: 0 $spacing-md;
  font-size: $font-base;
  color: $text-primary;
  border: 2rpx solid $border-light;
}
.error-text {
  font-size: $font-sm;
  color: $accent-danger;
  margin-bottom: $spacing-md;
}
.login-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: #fff;
  font-size: $font-md;
  font-weight: 600;
  border-radius: $radius-md;
  margin-top: $spacing-lg;
  &.disabled { opacity: 0.5; }
  &:active:not(.disabled) { opacity: 0.8; }
}
.links {
  text-align: center;
  margin-top: $spacing-xl;
}
.link {
  font-size: $font-sm;
  color: $accent-primary;
}
</style>
