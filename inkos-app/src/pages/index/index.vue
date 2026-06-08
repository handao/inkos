<template>
  <view class="home">
    <view class="header">
      <text class="greeting">你好, {{ userStore.profile?.name || '创作者' }}</text>
      <text class="subtitle">欢迎回到 InkOS</text>
    </view>

    <view class="stats-bar">
      <view class="stat-item" v-for="s in stats" :key="s.label">
        <text class="stat-value">{{ s.value }}</text>
        <text class="stat-label">{{ s.label }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">快捷操作</text>
      </view>
      <view class="quick-actions">
        <view class="action-item" @tap="goCreate">
          <view class="action-icon create">✏️</view>
          <text class="action-label">新建作品</text>
        </view>
        <view class="action-item" @tap="goLibrary">
          <view class="action-icon shelf">📚</view>
          <text class="action-label">我的文库</text>
        </view>
        <view class="action-item" @tap="continueLast">
          <view class="action-icon continue">▶️</view>
          <text class="action-label">继续写作</text>
        </view>
        <view class="action-item" @tap="goSearch">
          <view class="action-icon search">🔍</view>
          <text class="action-label">搜索作品</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">最近更新</text>
        <text class="section-more" @tap="goLibrary">查看全部 ›</text>
      </view>
      <view v-if="libraryStore.loading">
        <LoadingSpinner tip="加载中..." />
      </view>
      <view v-else-if="libraryStore.books.length === 0">
        <EmptyState
          icon="📖"
          title="还没有作品"
          desc="创建你的第一部AI小说吧"
          actionText="开始创作"
          @action="goCreate"
        />
      </view>
      <block v-else>
        <NovelCard v-for="book in libraryStore.ongoingBooks.slice(0, 3)" :key="book.id" :novel="bookToNovel(book)" />
      </block>
    </view>

    <view class="section" v-if="libraryStore.completedBooks.length > 0">
      <view class="section-header">
        <text class="section-title">已完成</text>
      </view>
      <NovelCard v-for="book in libraryStore.completedBooks.slice(0, 2)" :key="book.id" :novel="bookToNovel(book)" />
    </view>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useLibraryStore } from '@/stores/library'
import { useUserStore } from '@/stores/user'
import type { Book } from '@/api'

const libraryStore = useLibraryStore()
const userStore = useUserStore()

const stats = ref([
  { label: '作品', value: '--' },
  { label: '章节', value: '--' },
  { label: '总字数', value: '--' },
])

onShow(() => {
  libraryStore.fetchBooks()
  updateStats()
})

function updateStats() {
  const books = libraryStore.books
  const totalChapters = books.reduce((sum, b) => sum + b.chaptersWritten, 0)
  stats.value = [
    { label: '作品', value: String(books.length) },
    { label: '章节', value: String(totalChapters) },
    { label: '总字数', value: '--' },
  ]
}

function goCreate() { uni.navigateTo({ url: '/pages/create/create' }) }
function goLibrary() { uni.switchTab({ url: '/pages/library/index' }) }
function goSearch() { uni.navigateTo({ url: '/pages/search/search' }) }

function continueLast() {
  if (libraryStore.ongoingBooks.length > 0) {
    const last = libraryStore.ongoingBooks[0]
    uni.navigateTo({ url: `/pages/library/detail?id=${last.id}` })
  } else if (libraryStore.draftBooks.length > 0) {
    const last = libraryStore.draftBooks[0]
    uni.navigateTo({ url: `/pages/library/detail?id=${last.id}` })
  } else {
    goCreate()
  }
}

function bookToNovel(book: Book) {
  return {
    id: book.id,
    title: book.title,
    author: '',
    cover: book.coverImageUrl || '',
    description: '',
    genre: book.genre,
    status: book.status,
    chapterCount: book.chaptersWritten,
    wordCount: 0,
    createdAt: book.createdAt,
    updatedAt: book.updatedAt,
    tags: [],
    rating: 0,
  }
}
</script>

<style lang="scss" scoped>
.home {
  min-height: 100vh;
  background: $bg-primary;
}
.header {
  padding: $spacing-xl $spacing-lg $spacing-md;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: #fff;
}
.greeting { font-size: $font-xl; font-weight: 700; display: block; }
.subtitle { font-size: $font-sm; opacity: 0.8; margin-top: 4rpx; display: block; }
.stats-bar {
  display: flex;
  margin: -32rpx $spacing-md 0;
  background: $bg-card;
  border-radius: $radius-lg;
  padding: $spacing-md;
  box-shadow: $shadow-md;
  position: relative;
  z-index: 2;
}
.stat-item {
  flex: 1;
  text-align: center;
  border-right: 1rpx solid $border-light;
  &:last-child { border: none; }
}
.stat-value { font-size: $font-xl; font-weight: 700; color: $text-primary; display: block; }
.stat-label { font-size: $font-xs; color: $text-tertiary; margin-top: 4rpx; display: block; }
.section { margin-top: $spacing-lg; }
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 $spacing-lg $spacing-sm;
}
.section-title { font-size: $font-md; font-weight: 600; color: $text-primary; }
.section-more { font-size: $font-sm; color: $accent-primary; }
.quick-actions {
  display: flex;
  padding: 0 $spacing-md;
  gap: $spacing-sm;
}
.action-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-md 0;
  background: $bg-card;
  border-radius: $radius-md;
  box-shadow: $shadow-sm;
}
.action-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  &.create { background: rgba(59, 130, 246, 0.1); }
  &.shelf { background: rgba(16, 185, 129, 0.1); }
  &.continue { background: rgba(245, 158, 11, 0.1); }
  &.search { background: rgba(99, 102, 241, 0.1); }
}
.action-label { font-size: $font-xs; color: $text-secondary; }
</style>
