<template>
  <view class="loading-container" :style="{ padding: fullscreen ? '40vh 0' : '80rpx 0' }">
    <view class="spinner" :style="{ width: size + 'rpx', height: size + 'rpx' }">
      <view class="dot" v-for="i in 4" :key="i" :style="{ animationDelay: i * 0.15 + 's' }" />
    </view>
    <text class="tip" v-if="tip">{{ tip }}</text>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  tip?: string
  fullscreen?: boolean
  size?: number
}>(), { size: 60, fullscreen: false })
</script>

<style lang="scss" scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $spacing-md;
}
.spinner {
  display: flex;
  gap: 8rpx;
  align-items: center;
  justify-content: center;
}
.dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: $accent-primary;
  animation: bounce 1.2s ease-in-out infinite;
}
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}
.tip {
  font-size: $font-sm;
  color: $text-tertiary;
}
</style>
