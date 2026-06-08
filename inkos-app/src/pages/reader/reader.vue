<template>
  <view class="reader" :style="readerStyle">
    <view class="reader-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left" @tap="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="header-title">{{ chapter?.title || '阅读' }}</text>
      <view class="header-right" @tap="showSettings = !showSettings">
        <text class="settings-icon">⚙</text>
      </view>
    </view>

    <view v-if="loading" class="loading-wrap">
      <LoadingSpinner tip="加载章节..." />
    </view>

    <scroll-view
      v-else
      class="content-scroll"
      scroll-y
      @scroll="onScroll"
      :scroll-top="scrollTop"
    >
      <view class="content-wrap">
        <text class="chapter-title">{{ chapter?.title || '无标题' }}</text>
        <text class="chapter-content">{{ chapter?.content || '暂无内容' }}</text>
      </view>
      <view class="content-footer">
        <text class="footer-info">— {{ novelTitle }} · 第{{ chapter?.chapterNumber }}章 —</text>
        <text class="footer-words">{{ formatWordCount(chapter?.wordCount || 0) }}字</text>
      </view>
      <view class="safe-area-bottom" />
    </scroll-view>

    <view class="reader-footer" v-if="!loading">
      <view class="footer-btn" @tap="prevChapter">‹ 上一章</view>
      <view class="footer-center">
        <text>{{ chapter?.chapterNumber }}/{{ totalChapters }}</text>
      </view>
      <view class="footer-btn" @tap="nextChapter">下一章 ›</view>
    </view>

    <view class="settings-panel" v-if="showSettings" @tap.stop>
      <view class="settings-mask" @tap="showSettings = false" />
      <view class="settings-content">
        <text class="settings-title">阅读设置</text>
        <view class="setting-row">
          <text class="setting-label">字号</text>
          <view class="size-control">
            <text class="size-btn" @tap="changeFontSize(-2)">A-</text>
            <text class="size-value">{{ fontSize }}</text>
            <text class="size-btn" @tap="changeFontSize(2)">A+</text>
          </view>
        </view>
        <view class="setting-row">
          <text class="setting-label">主题</text>
          <view class="theme-options">
            <view
              v-for="t in themes"
              :key="t.id"
              class="theme-dot"
              :style="{ background: t.color }"
              :class="{ active: readerTheme === t.id }"
              @tap="readerTheme = t.id"
            />
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { api, type Chapter, formatWordCount } from '@/api'
const novelId = ref('')
const chapterId = ref('')
const chapter = ref<Chapter | null>(null)
const loading = ref(true)
const scrollTop = ref(0)
const showSettings = ref(false)
const fontSize = ref(36)
const readerTheme = ref('beige')
const totalChapters = ref(0)
const novelTitle = ref('')
const chapters = ref<Chapter[]>([])
const statusBarHeight = ref(44)

const themes = [
  { id: 'beige', color: '#f5f0e8', bg: '#f5f0e8', text: '#3a3a3a' },
  { id: 'gray', color: '#e8e8e8', bg: '#e8e8e8', text: '#333' },
  { id: 'dark', color: '#1a1a2e', bg: '#1a1a2e', text: '#c8c8d0' },
  { id: 'green', color: '#c8e6c9', bg: '#c8e6c9', text: '#2e5c2e' },
]

const readerStyle = computed(() => {
  const t = themes.find(x => x.id === readerTheme.value) || themes[0]
  return {
    backgroundColor: t.bg,
    color: t.text,
    '--reader-text': t.text,
    '--reader-bg': t.bg,
  }
})

onMounted(() => {
  try {
    const sys = uni.getSystemInfoSync()
    statusBarHeight.value = sys.statusBarHeight || 44
  } catch {}
  const saved = uni.getStorageSync('reader-font-size')
  if (saved) fontSize.value = parseInt(saved)
  const savedTheme = uni.getStorageSync('reader-theme')
  if (savedTheme) readerTheme.value = savedTheme

  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  if (page?.options) {
    novelId.value = page.options.novelId || ''
    chapterId.value = page.options.chapterId || ''
  }
  if (novelId.value && chapterId.value) {
    loadChapter()
  }
})

function changeFontSize(delta: number) {
  fontSize.value = Math.max(24, Math.min(56, fontSize.value + delta))
  uni.setStorageSync('reader-font-size', fontSize.value)
}

watch(readerTheme, (val) => {
  uni.setStorageSync('reader-theme', val)
})

async function loadChapter() {
  loading.value = true
  try {
    chapter.value = await api.chapters.get(novelId.value, chapterId.value)
    chapters.value = await api.chapters.list(novelId.value)
    totalChapters.value = chapters.value.length
    const book = await api.books.get(novelId.value)
    novelTitle.value = book.title
    scrollTop.value = 0
  } catch (e: any) {
    uni.showToast({ title: '加载失败: ' + e.message, icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function goToChapter(id: string) {
  chapterId.value = id
  await loadChapter()
}

function nextChapter() {
  const idx = chapters.value.findIndex(c => c.id === chapterId.value)
  if (idx < chapters.value.length - 1) {
    goToChapter(chapters.value[idx + 1].id)
  } else {
    uni.showToast({ title: '已是最后一章', icon: 'none' })
  }
}

function prevChapter() {
  const idx = chapters.value.findIndex(c => c.id === chapterId.value)
  if (idx > 0) {
    goToChapter(chapters.value[idx - 1].id)
  } else {
    uni.showToast({ title: '已是第一章', icon: 'none' })
  }
}

function onScroll(e: any) {
  scrollTop.value = e.detail.scrollTop
}

function goBack() { uni.navigateBack() }
</script>

<style lang="scss" scoped>
.reader {
  min-height: 100vh;
  transition: background 0.3s;
  display: flex;
  flex-direction: column;
}
.reader-header {
  display: flex;
  align-items: center;
  padding: 0 $spacing-md;
  height: 88rpx;
  background: var(--reader-bg);
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1rpx solid rgba(0,0,0,0.05);
}
.header-left, .header-right {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}
.back-icon { font-size: 48rpx; font-weight: 300; line-height: 1; color: var(--reader-text); }
.settings-icon { font-size: 36rpx; color: var(--reader-text); }
.header-title {
  flex: 1;
  text-align: center;
  font-size: $font-sm;
  color: var(--reader-text);
  lines: 1;
  text-overflow: ellipsis;
}
.loading-wrap { padding-top: 300rpx; }
.content-scroll {
  flex: 1;
  padding: 0 $spacing-lg;
}
.content-wrap {
  padding: $spacing-xl 0;
}
.chapter-title {
  display: block;
  font-size: 44rpx;
  font-weight: 700;
  text-align: center;
  color: var(--reader-text);
  margin-bottom: $spacing-xl;
  line-height: 1.5;
}
.chapter-content {
  font-size: v-bind('fontSize + "rpx"');
  color: var(--reader-text);
  line-height: 1.9;
  text-align: justify;
  white-space: pre-wrap;
  word-break: break-word;
  display: block;
}
.content-footer {
  text-align: center;
  padding: $spacing-xl 0;
  color: var(--reader-text);
  opacity: 0.5;
  font-size: 24rpx;
}
.footer-info { display: block; }
.footer-words { display: block; margin-top: $spacing-xs; }
.reader-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-sm $spacing-lg;
  background: var(--reader-bg);
  border-top: 1rpx solid rgba(0,0,0,0.05);
}
.footer-btn {
  font-size: $font-sm;
  color: var(--reader-text);
  padding: $spacing-xs $spacing-md;
}
.footer-center {
  font-size: $font-xs;
  color: var(--reader-text);
  opacity: 0.6;
}
.settings-panel {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}
.settings-mask {
  flex: 1;
  background: rgba(0,0,0,0.3);
}
.settings-content {
  background: $bg-secondary;
  border-radius: $radius-xl $radius-xl 0 0;
  padding: $spacing-xl $spacing-lg calc($spacing-xl + $safe-bottom);
}
.settings-title {
  font-size: $font-lg;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: $spacing-md;
}
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-sm 0;
}
.setting-label { font-size: $font-sm; color: $text-secondary; }
.size-control { display: flex; align-items: center; gap: $spacing-sm; }
.size-btn {
  width: 56rpx; height: 56rpx; border-radius: 50%;
  background: $bg-tertiary; display: flex; align-items: center; justify-content: center;
  font-size: $font-sm; color: $text-primary;
}
.size-value { font-size: $font-base; color: $text-primary; min-width: 48rpx; text-align: center; }
.theme-options { display: flex; gap: $spacing-sm; }
.theme-dot {
  width: 48rpx; height: 48rpx; border-radius: 50%;
  border: 3rpx solid transparent;
  &.active { border-color: $accent-primary; }
}
</style>
