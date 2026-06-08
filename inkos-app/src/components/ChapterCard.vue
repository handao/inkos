<template>
  <view class="chapter-card" @tap="goReader">
    <view class="left">
      <text class="number">{{ chapter.chapterNumber }}</text>
    </view>
    <view class="right">
      <text class="title">{{ chapter.title || `第${chapter.chapterNumber}章` }}</text>
      <view class="meta">
        <text class="words">{{ formatWordCount(chapter.wordCount) }}字</text>
        <text class="status" :class="chapter.status">{{ statusText }}</text>
        <text class="date">{{ formatDate(chapter.updatedAt) }}</text>
      </view>
    </view>
    <text class="arrow">›</text>
  </view>
</template>

<script setup lang="ts">
import type { Chapter } from '@/api'
import { formatWordCount, formatDate } from '@/api'

const props = defineProps<{ chapter: Chapter; novelId: string }>()

const statusText = {
  draft: '草稿', generated: '已生成', revised: '已修订', published: '已发布',
}[props.chapter.status] || props.chapter.status

function goReader() {
  uni.navigateTo({
    url: `/pages/reader/reader?novelId=${props.novelId}&chapterId=${props.chapter.id}`,
  })
}
</script>

<style lang="scss" scoped>
.chapter-card {
  display: flex;
  align-items: center;
  padding: $spacing-md $spacing-lg;
  background: $bg-card;
  border-bottom: 1rpx solid $border-light;
  gap: $spacing-md;
  &:active { background: $bg-tertiary; }
}
.left {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: $bg-tertiary;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.number {
  font-size: $font-sm;
  font-weight: 600;
  color: $text-secondary;
}
.right {
  flex: 1;
  min-width: 0;
}
.title {
  font-size: $font-base;
  color: $text-primary;
  font-weight: 500;
  display: block;
  lines: 1;
  text-overflow: ellipsis;
}
.meta {
  display: flex;
  gap: $spacing-sm;
  margin-top: 4rpx;
  font-size: $font-xs;
  color: $text-tertiary;
}
.status {
  &.generated { color: $accent-primary; }
  &.revised { color: $accent-secondary; }
  &.published { color: $accent-success; }
  &.draft { color: $accent-warm; }
}
.arrow {
  font-size: $font-lg;
  color: $text-tertiary;
}
</style>
