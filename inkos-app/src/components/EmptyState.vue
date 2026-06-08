<template>
  <view class="empty-state">
    <text class="icon">{{ icon }}</text>
    <text class="title">{{ title }}</text>
    <text class="desc" v-if="desc">{{ desc }}</text>
    <view class="action" v-if="actionText" @tap="$emit('action')">
      {{ actionText }}
    </view>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  icon?: string
  title: string
  desc?: string
  actionText?: string
}>(), { icon: '📝' })

defineEmits<{ action: [] }>()
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
}
.icon {
  font-size: 80rpx;
  margin-bottom: $spacing-md;
}
.title {
  font-size: $font-md;
  color: $text-secondary;
  font-weight: 500;
}
.desc {
  font-size: $font-sm;
  color: $text-tertiary;
  margin-top: $spacing-sm;
  text-align: center;
}
.action {
  margin-top: $spacing-lg;
  padding: 16rpx 48rpx;
  background: $accent-primary;
  color: $text-inverse;
  border-radius: 40rpx;
  font-size: $font-base;
}
</style>
