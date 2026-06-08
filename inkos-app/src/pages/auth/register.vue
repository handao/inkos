<template>
  <view class="register-page">
    <view class="header">
      <text class="back-btn" @tap="goBack">‹</text>
      <text class="title">注册账号</text>
      <text class="step">第{{ step }}/2步</text>
    </view>

    <view class="form">
      <template v-if="step === 1">
        <view class="form-group">
          <text class="label">邮箱</text>
          <input
            class="input"
            v-model="email"
            type="email"
            placeholder="请输入邮箱地址"
            :disabled="sending"
          />
        </view>

        <view class="send-code-btn" :class="{ disabled: !email.trim() }" @tap="handleSendCode">
          {{ sending ? '发送中...' : '发送验证码' }}
        </view>

        <view class="error-text" v-if="error">{{ error }}</view>

        <view class="next-btn" :class="{ disabled: !codeSent }" @tap="step = 2">
          下一步
        </view>
      </template>

      <template v-else>
        <view class="form-group">
          <text class="label">验证码</text>
          <input
            class="input"
            v-model="code"
            type="text"
            placeholder="请输入邮箱验证码"
            :disabled="loading"
          />
        </view>

        <view class="form-group">
          <text class="label">昵称</text>
          <input
            class="input"
            v-model="nickname"
            type="text"
            placeholder="给自己起个名字"
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
            placeholder="至少 6 位密码"
            :disabled="loading"
          />
        </view>

        <view class="form-group">
          <text class="label">确认密码</text>
          <input
            class="input"
            v-model="confirmPassword"
            type="password"
            password
            placeholder="再次输入密码"
            :disabled="loading"
          />
        </view>

        <view class="error-text" v-if="error">{{ error }}</view>

        <view class="register-btn" :class="{ disabled: !canRegister }" @tap="handleRegister">
          {{ loading ? '注册中...' : '完成注册' }}
        </view>
      </template>

      <view class="links">
        <text class="link" @tap="goLogin">已有账号？立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const step = ref(1)
const email = ref('')
const code = ref('')
const nickname = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const loading = ref(false)
const sending = ref(false)
const codeSent = ref(false)

const canRegister = computed(() =>
  code.value.trim().length > 0 &&
  nickname.value.trim().length > 0 &&
  password.value.length >= 6 &&
  password.value === confirmPassword.value
)

async function handleSendCode() {
  if (!email.value.trim() || sending.value) return
  sending.value = true
  error.value = ''
  try {
    await userStore.sendCode(email.value.trim())
    codeSent.value = true
    uni.showToast({ title: '验证码已发送', icon: 'success' })
  } catch (e: any) {
    error.value = e.message || '发送失败'
  } finally {
    sending.value = false
  }
}

async function handleRegister() {
  if (!canRegister.value || loading.value) return
  loading.value = true
  error.value = ''
  try {
    await userStore.register(email.value.trim(), password.value, nickname.value.trim(), code.value.trim())
    uni.switchTab({ url: '/pages/index/index' })
  } catch (e: any) {
    error.value = e.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}

function goBack() { uni.navigateBack() }
function goLogin() { uni.redirectTo({ url: '/pages/auth/login' }) }
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: $bg-primary;
}
.header {
  display: flex;
  align-items: center;
  padding: 120rpx $spacing-lg $spacing-lg;
  background: $bg-secondary;
}
.back-btn {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
  width: 60rpx;
}
.title {
  flex: 1;
  font-size: $font-xl;
  font-weight: 700;
  color: $text-primary;
  text-align: center;
}
.step {
  font-size: $font-xs;
  color: $accent-primary;
  background: rgba(59, 130, 246, 0.1);
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.form {
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
  background: $bg-card;
  border-radius: $radius-md;
  padding: 0 $spacing-md;
  font-size: $font-base;
  color: $text-primary;
  border: 2rpx solid $border-light;
}
.send-code-btn, .next-btn, .register-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: #fff;
  font-size: $font-md;
  font-weight: 600;
  border-radius: $radius-md;
  margin-top: $spacing-md;
  &.disabled { opacity: 0.5; }
  &:active:not(.disabled) { opacity: 0.8; }
}
.error-text {
  font-size: $font-sm;
  color: $accent-danger;
  margin-top: $spacing-md;
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
