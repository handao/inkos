<template>
  <view class="genre-picker">
    <view
      class="genre-item"
      :class="{ active: modelValue === genre.value }"
      v-for="genre in genres"
      :key="genre.value"
      @tap="$emit('update:modelValue', genre.value)"
    >
      <text class="genre-icon">{{ genre.icon }}</text>
      <text class="genre-label">{{ genre.label }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
defineProps<{ modelValue: string }>()
defineEmits<{ 'update:modelValue': [value: string] }>()

const genres = [
  { value: 'fantasy', label: '玄幻', icon: '🐉' },
  { value: 'xianxia', label: '仙侠', icon: '⚔️' },
  { value: 'wuxia', label: '武侠', icon: '🗡️' },
  { value: 'scifi', label: '科幻', icon: '🚀' },
  { value: 'horror', label: '恐怖', icon: '👻' },
  { value: 'romance', label: '言情', icon: '💕' },
  { value: 'urban', label: '都市', icon: '🏙️' },
  { value: 'history', label: '历史', icon: '📜' },
  { value: 'gaming', label: '游戏', icon: '🎮' },
  { value: 'fanfic', label: '同人', icon: '🔄' },
  { value: 'original', label: '原创', icon: '✨' },
]
</script>

<style lang="scss" scoped>
.genre-picker {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-sm;
  padding: $spacing-md 0;
}
.genre-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: $spacing-sm;
  border-radius: $radius-md;
  background: $bg-secondary;
  border: 2rpx solid $border-light;
  transition: all 0.2s;
  &.active {
    border-color: $accent-primary;
    background: rgba(59, 130, 246, 0.06);
  }
  &:active { transform: scale(0.95); }
}
.genre-icon { font-size: 40rpx; }
.genre-label { font-size: $font-xs; color: $text-secondary; }
</style>
