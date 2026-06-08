<template>
  <view class="library">
    <view class="tabs">
      <view
        class="tab"
        :class="{ active: activeTab === 'ongoing' }"
        @tap="activeTab = 'ongoing'"
      >连载中 ({{ libraryStore.ongoingBooks.length }})</view>
      <view
        class="tab"
        :class="{ active: activeTab === 'draft' }"
        @tap="activeTab = 'draft'"
      >草稿 ({{ libraryStore.draftBooks.length }})</view>
      <view
        class="tab"
        :class="{ active: activeTab === 'completed' }"
        @tap="activeTab = 'completed'"
      >已完成 ({{ libraryStore.completedBooks.length }})</view>
    </view>

    <view v-if="libraryStore.loading" class="loading-wrap">
      <LoadingSpinner tip="加载文库..." />
    </view>

    <view v-else-if="currentBooks.length === 0" class="empty-wrap">
      <EmptyState
        icon="📚"
        title="这里还没有作品"
        :desc="emptyDesc"
        actionText="新建作品"
        @action="goCreate"
      />
    </view>

    <scroll-view
      v-else
      class="book-list"
      scroll-y
      @scrolltolower="loadMore"
      :style="{ height: scrollHeight + 'px' }"
    >
      <NovelCard v-for="book in currentBooks" :key="book.id" :novel="bookToNovel(book)" />
      <view class="safe-area-bottom" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow, onReady } from '@dcloudio/uni-app'
import { useLibraryStore } from '@/stores/library'
import type { Book } from '@/api'

const libraryStore = useLibraryStore()
const activeTab = ref<'ongoing' | 'draft' | 'completed'>('ongoing')
const scrollHeight = ref(600)

const currentBooks = computed(() => {
  const map = {
    ongoing: libraryStore.ongoingBooks,
    draft: libraryStore.draftBooks,
    completed: libraryStore.completedBooks,
  }
  return map[activeTab.value]
})

const emptyDesc = computed(() => {
  const map = {
    ongoing: '还没有连载中的作品，开始创作吧',
    draft: '还没有草稿作品',
    completed: '还没有完成的作品',
  }
  return map[activeTab.value]
})

onShow(() => {
  libraryStore.fetchBooks()
})

onReady(() => {
  try {
    const sys = uni.getSystemInfoSync()
    const tabBarHeight = 50
    const statusBar = sys.statusBarHeight || 44
    const windowH = sys.windowHeight || sys.screenHeight
    scrollHeight.value = windowH - statusBar - tabBarHeight - 88
  } catch {}
})

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

function loadMore() {}
function goCreate() { uni.navigateTo({ url: '/pages/create/create' }) }
</script>

<style lang="scss" scoped>
.library {
  min-height: 100vh;
  background: $bg-primary;
}
.tabs {
  display: flex;
  background: $bg-secondary;
  padding: 0 $spacing-md;
  border-bottom: 1rpx solid $border-light;
  position: sticky;
  top: 0;
  z-index: 10;
}
.tab {
  flex: 1;
  text-align: center;
  padding: $spacing-md 0;
  font-size: $font-sm;
  color: $text-tertiary;
  position: relative;
  &.active {
    color: $accent-primary;
    font-weight: 600;
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 40rpx;
      height: 4rpx;
      border-radius: 2rpx;
      background: $accent-primary;
    }
  }
}
.loading-wrap { padding-top: 200rpx; }
.empty-wrap { padding-top: 100rpx; }
.book-list { padding-top: $spacing-sm; }
</style>
