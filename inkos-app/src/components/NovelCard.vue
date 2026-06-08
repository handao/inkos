<template>
  <view class="novel-card" @tap="goDetail">
    <image class="cover" :src="novel.cover || '/static/default-cover.svg'" mode="aspectFill" />
    <view class="info">
      <text class="title">{{ novel.title }}</text>
      <text class="author">{{ novel.author || 'InkOS' }}</text>
      <view class="meta">
        <text class="tag">{{ genreLabel }}</text>
        <text class="count">{{ formatWordCount(novel.wordCount) }}字</text>
      </view>
      <view class="status-badge" :class="novel.status">
        {{ statusLabel }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { formatWordCount } from '@/api'

export interface NovelLike {
  id: string
  title: string
  author: string
  cover: string
  description: string
  genre: string
  status: 'draft' | 'ongoing' | 'completed'
  chapterCount: number
  wordCount: number
  createdAt: string
  updatedAt: string
  tags: string[]
  rating: number
}

const props = defineProps<{ novel: NovelLike }>()

const genreLabel = {
  fantasy: '玄幻', xianxia: '仙侠', wuxia: '武侠',
  scifi: '科幻', horror: '恐怖', romance: '言情',
  urban: '都市', history: '历史', gaming: '游戏',
  fanfic: '同人', original: '原创',
}[props.novel.genre] || props.novel.genre

const statusLabel = {
  draft: '草稿', ongoing: '连载中', completed: '已完结',
}[props.novel.status] || props.novel.status

function goDetail() {
  uni.navigateTo({ url: `/pages/library/detail?id=${props.novel.id}` })
}
</script>

<style lang="scss" scoped>
.novel-card {
  display: flex;
  background: $bg-card;
  border-radius: $radius-md;
  padding: $spacing-md;
  margin: 0 $spacing-md $spacing-md;
  box-shadow: $shadow-sm;
  gap: $spacing-md;
  transition: transform 0.2s;
  &:active { transform: scale(0.98); }
}
.cover {
  width: 160rpx;
  height: 220rpx;
  border-radius: $radius-sm;
  flex-shrink: 0;
  background: $bg-tertiary;
}
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  min-width: 0;
}
.title {
  font-size: $font-md;
  font-weight: 600;
  color: $text-primary;
  lines: 1;
  text-overflow: ellipsis;
}
.author {
  font-size: $font-sm;
  color: $text-tertiary;
}
.meta {
  display: flex;
  gap: $spacing-sm;
  align-items: center;
}
.tag {
  font-size: $font-xs;
  color: $accent-primary;
  background: rgba(59, 130, 246, 0.1);
  padding: 2rpx 12rpx;
  border-radius: 20rpx;
}
.count {
  font-size: $font-xs;
  color: $text-tertiary;
}
.status-badge {
  align-self: flex-start;
  font-size: $font-xs;
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
  background: $bg-tertiary;
  color: $text-secondary;
  &.ongoing { background: rgba(16, 185, 129, 0.1); color: $accent-success; }
  &.completed { background: rgba(99, 102, 241, 0.1); color: $accent-secondary; }
  &.draft { background: rgba(245, 158, 11, 0.1); color: $accent-warm; }
}
</style>
