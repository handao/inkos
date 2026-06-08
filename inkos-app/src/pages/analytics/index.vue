<template>
  <view class="analytics">
    <PageNav title="数据分析" :showBack="false" />

    <view class="stats-bar">
      <view class="stat-item">
        <text class="stat-value">{{ stats.totalBooks }}</text>
        <text class="stat-label">总作品</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.totalChapters }}</text>
        <text class="stat-label">总章节</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.totalWords }}</text>
        <text class="stat-label">总字数</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">写作趋势</text>
      <view class="chart-placeholder">
        <text>📊 写作趋势图表</text>
        <text class="placeholder-desc">对接后端数据后将展示每日字数统计</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">作品分布</text>
      <view class="chart-placeholder">
        <text>🥧 作品类型分布</text>
        <text class="placeholder-desc">按题材分类展示作品占比</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">写作统计</text>
      <view class="stats-grid">
        <view class="stats-card">
          <text class="stats-card-value">{{ stats.draftCount }}</text>
          <text class="stats-card-label">草稿中</text>
        </view>
        <view class="stats-card">
          <text class="stats-card-value">{{ stats.ongoingCount }}</text>
          <text class="stats-card-label">连载中</text>
        </view>
        <view class="stats-card">
          <text class="stats-card-value">{{ stats.completedCount }}</text>
          <text class="stats-card-label">已完成</text>
        </view>
        <view class="stats-card">
          <text class="stats-card-value">{{ stats.totalSessions }}</text>
          <text class="stats-card-label">AI 会话</text>
        </view>
      </view>
    </view>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useLibraryStore } from '@/stores/library'

const libraryStore = useLibraryStore()

const stats = ref({
  totalBooks: 0,
  totalChapters: 0,
  totalWords: '0',
  draftCount: 0,
  ongoingCount: 0,
  completedCount: 0,
  totalSessions: 0,
})

onShow(() => {
  libraryStore.fetchBooks().then(() => {
    const b = libraryStore.books
    stats.value = {
      totalBooks: b.length,
      totalChapters: b.reduce((s, x) => s + x.chaptersWritten, 0),
      totalWords: '--',
      draftCount: libraryStore.draftBooks.length,
      ongoingCount: libraryStore.ongoingBooks.length,
      completedCount: libraryStore.completedBooks.length,
      totalSessions: 0,
    }
  })
})
</script>

<style lang="scss" scoped>
.analytics {
  min-height: 100vh;
  background: $bg-primary;
}
.stats-bar {
  display: flex;
  margin: $spacing-md;
  background: $bg-card;
  border-radius: $radius-lg;
  padding: $spacing-md;
  box-shadow: $shadow-sm;
}
.stat-item {
  flex: 1;
  text-align: center;
  border-right: 1rpx solid $border-light;
  &:last-child { border: none; }
}
.stat-value { font-size: $font-xl; font-weight: 700; color: $text-primary; display: block; }
.stat-label { font-size: $font-xs; color: $text-tertiary; margin-top: 4rpx; display: block; }
.section {
  margin: $spacing-md;
}
.section-title {
  font-size: $font-md;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: $spacing-sm;
}
.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 240rpx;
  background: $bg-card;
  border-radius: $radius-md;
  border: 2rpx dashed $border-light;
  font-size: $font-sm;
  color: $text-secondary;
  gap: $spacing-sm;
}
.placeholder-desc {
  font-size: $font-xs;
  color: $text-tertiary;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-sm;
}
.stats-card {
  background: $bg-card;
  border-radius: $radius-md;
  padding: $spacing-md;
  text-align: center;
}
.stats-card-value {
  font-size: $font-xl;
  font-weight: 700;
  color: $accent-primary;
  display: block;
}
.stats-card-label {
  font-size: $font-xs;
  color: $text-tertiary;
  margin-top: 4rpx;
  display: block;
}
</style>
