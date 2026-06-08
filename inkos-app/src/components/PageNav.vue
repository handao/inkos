<template>
  <view class="page-nav" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="nav-bar">
      <view class="back" @tap="goBack" v-if="showBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="nav-title">{{ title }}</text>
      <view class="nav-right">
        <slot name="right" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

withDefaults(defineProps<{
  title: string
  showBack?: boolean
}>(), { showBack: true })

const statusBarHeight = ref(44)

onMounted(() => {
  try {
    const sys = uni.getSystemInfoSync()
    statusBarHeight.value = sys.statusBarHeight || 44
  } catch {}
})

function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.page-nav {
  background: $bg-secondary;
  position: sticky;
  top: 0;
  z-index: 100;
}
.nav-bar {
  height: 88rpx;
  display: flex;
  align-items: center;
  padding: 0 $spacing-md;
  position: relative;
}
.back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}
.back-icon {
  font-size: 48rpx;
  color: $text-primary;
  font-weight: 300;
  line-height: 1;
}
.nav-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: $font-md;
  font-weight: 600;
  color: $text-primary;
  white-space: nowrap;
}
.nav-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  z-index: 10;
}
</style>
