<template>
  <view class="detail">
    <PageNav :title="book?.title || '作品详情'" />

    <view v-if="libraryStore.loading && !book" class="loading-wrap">
      <LoadingSpinner tip="加载中..." />
    </view>

    <template v-else-if="book">
      <view class="hero">
        <image class="cover" :src="book.coverImageUrl || '/static/default-cover.svg'" mode="aspectFill" />
        <view class="hero-info">
          <text class="title">{{ book.title }}</text>
          <view class="tags">
            <text class="tag">{{ genreLabel }}</text>
            <text class="tag status" :class="book.status">{{ statusLabel }}</text>
          </view>
          <view class="metrics">
            <text class="metric">{{ book.chaptersWritten }} 章</text>
          </view>
        </view>
      </view>

      <view class="actions">
        <view class="action-btn primary" @tap="goWorkspace">
          <text>✍️ AI 写作</text>
        </view>
        <view class="action-btn" @tap="startReading">
          <text>📖 开始阅读</text>
        </view>
      </view>

      <view class="section">
        <view class="section-header">
          <text class="section-title">章节列表 ({{ chapters.length }})</text>
        </view>
        <view v-if="chapters.length === 0" class="no-chapters">
          <text>还没有章节，前往 AI 写作工作台开始创作</text>
        </view>
        <ChapterCard
          v-for="ch in chapters"
          :key="ch.id"
          :chapter="ch"
          :novel-id="book.id"
        />
      </view>
    </template>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useLibraryStore } from '@/stores/library'
import { api, type Chapter } from '@/api'

const libraryStore = useLibraryStore()

const bookId = ref('')
const chapters = ref<Chapter[]>([])

const book = computed(() => libraryStore.currentBook)

const genreLabel = computed(() => {
  const m: Record<string, string> = {
    fantasy: '玄幻', xianxia: '仙侠', wuxia: '武侠',
    scifi: '科幻', horror: '恐怖', romance: '言情',
    urban: '都市', history: '历史', gaming: '游戏',
    fanfic: '同人', original: '原创',
  }
  return m[book.value?.genre || ''] || book.value?.genre || '未知'
})

const statusLabel = computed(() => {
  const m: Record<string, string> = { draft: '草稿', ongoing: '连载中', completed: '已完结' }
  return m[book.value?.status || ''] || '未知'
})

onShow(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  if (page?.$page?.fullPath) {
    const match = page.$page.fullPath.match(/id=([^&]+)/)
    if (match) bookId.value = decodeURIComponent(match[1])
  }
  if (page?.options?.id) {
    bookId.value = page.options.id
  }
  if (bookId.value) {
    libraryStore.fetchBook(bookId.value)
    loadChapters()
  }
})

async function loadChapters() {
  if (!bookId.value) return
  try {
    chapters.value = await api.chapters.list(bookId.value)
  } catch {}
}

function goWorkspace() {
  uni.navigateTo({ url: `/pages/workspace/index?bookId=${bookId.value}` })
}

function startReading() {
  if (chapters.value.length === 0) {
    goWorkspace()
    return
  }
  const first = chapters.value[0]
  uni.navigateTo({
    url: `/pages/reader/reader?novelId=${bookId.value}&chapterId=${first.id}`,
  })
}
</script>

<style lang="scss" scoped>
.detail { min-height: 100vh; background: $bg-primary; }
.loading-wrap { padding-top: 200rpx; }
.hero {
  display: flex;
  gap: $spacing-md;
  padding: $spacing-lg;
  background: $bg-card;
}
.cover {
  width: 180rpx;
  height: 250rpx;
  border-radius: $radius-sm;
  flex-shrink: 0;
  background: $bg-tertiary;
}
.hero-info { flex: 1; display: flex; flex-direction: column; gap: $spacing-xs; min-width: 0; }
.hero-info .title { font-size: $font-lg; font-weight: 700; color: $text-primary; lines: 2; text-overflow: ellipsis; }
.tags { display: flex; gap: $spacing-sm; }
.tag {
  font-size: $font-xs; padding: 2rpx 14rpx; border-radius: 20rpx;
  background: rgba(59, 130, 246, 0.1); color: $accent-primary;
  &.status { background: $bg-tertiary; color: $text-secondary; }
  &.status.ongoing { background: rgba(16, 185, 129, 0.1); color: $accent-success; }
  &.status.completed { background: rgba(99, 102, 241, 0.1); color: $accent-secondary; }
}
.metrics { display: flex; gap: $spacing-sm; align-items: center; margin-top: auto; }
.metric { font-size: $font-sm; color: $text-secondary; }
.actions {
  display: flex;
  gap: $spacing-md;
  padding: $spacing-md $spacing-lg;
}
.action-btn {
  flex: 1;
  text-align: center;
  padding: $spacing-sm;
  border-radius: $radius-md;
  border: 2rpx solid $border-light;
  font-size: $font-sm;
  color: $text-primary;
  background: $bg-card;
  &.primary {
    background: $accent-primary;
    border-color: $accent-primary;
    color: #fff;
  }
  &:active { opacity: 0.8; }
}
.section { margin-top: $spacing-md; padding: 0 $spacing-lg; }
.section-title { font-size: $font-md; font-weight: 600; color: $text-primary; display: block; }
.section-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: $spacing-sm;
}
.no-chapters {
  text-align: center; padding: $spacing-xl 0;
  font-size: $font-sm; color: $text-tertiary;
}
</style>
