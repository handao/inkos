<template>
  <view class="search-page">
    <view class="search-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="query"
          placeholder="搜索作品、标签..."
          confirm-type="search"
          @confirm="doSearch"
          :focus="autoFocus"
        />
        <text class="clear-btn" v-if="query" @tap="query = ''">✕</text>
      </view>
      <text class="cancel-btn" @tap="goBack">取消</text>
    </view>

    <view v-if="!query && !results.length" class="suggestions">
      <view class="suggestion-section">
        <text class="suggestion-title">热门分类</text>
        <view class="genre-grid">
          <view
            class="genre-chip"
            v-for="g in hotGenres"
            :key="g.value"
            @tap="query = g.value; doSearch()"
          >
            <text>{{ g.icon }}</text>
            <text>{{ g.label }}</text>
          </view>
        </view>
      </view>
      <view class="suggestion-section">
        <text class="suggestion-title">最近搜索</text>
        <view v-if="recentSearches.length === 0">
          <text class="no-recent">暂无搜索记录</text>
        </view>
        <view v-else>
          <view
            class="recent-item"
            v-for="s in recentSearches"
            :key="s"
            @tap="query = s; doSearch()"
          >
            <text>🕐</text>
            <text>{{ s }}</text>
            <text class="remove-btn" @tap.stop="removeRecent(s)">✕</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="searching" class="loading-wrap">
      <LoadingSpinner tip="搜索中..." />
    </view>

    <view v-else-if="results.length > 0" class="results">
      <view class="result-header">
        <text>找到 {{ results.length }} 个结果</text>
      </view>
      <NovelCard v-for="novel in results" :key="novel.id" :novel="novel" />
    </view>

    <view v-else-if="hasSearched && !searching" class="no-results">
      <EmptyState icon="🔍" title="没有找到相关作品" desc="换个关键词试试" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, type Novel } from '@/api'

const query = ref('')
const results = ref<Novel[]>([])
const searching = ref(false)
const hasSearched = ref(false)
const autoFocus = ref(true)
const recentSearches = ref<string[]>([])
const statusBarHeight = ref(44)

const hotGenres = [
  { value: '玄幻', icon: '🐉', label: '玄幻' },
  { value: '仙侠', icon: '⚔️', label: '仙侠' },
  { value: '言情', icon: '💕', label: '言情' },
  { value: '科幻', icon: '🚀', label: '科幻' },
  { value: '都市', icon: '🏙️', label: '都市' },
  { value: '同人', icon: '🔄', label: '同人' },
]

onMounted(() => {
  try {
    const sys = uni.getSystemInfoSync()
    statusBarHeight.value = sys.statusBarHeight || 44
  } catch {}
  const saved = uni.getStorageSync('recent-searches')
  if (saved) {
    try { recentSearches.value = JSON.parse(saved) } catch {}
  }
  setTimeout(() => { autoFocus.value = true }, 300)
})

function saveRecent() {
  const q = query.value.trim()
  if (!q) return
  recentSearches.value = [q, ...recentSearches.value.filter(s => s !== q)].slice(0, 10)
  uni.setStorageSync('recent-searches', JSON.stringify(recentSearches.value))
}

function removeRecent(s: string) {
  recentSearches.value = recentSearches.value.filter(x => x !== s)
  uni.setStorageSync('recent-searches', JSON.stringify(recentSearches.value))
}

async function doSearch() {
  const q = query.value.trim()
  if (!q) return
  searching.value = true
  hasSearched.value = true
  saveRecent()
  try {
    results.value = await api.search(q)
  } catch (e: any) {
    results.value = []
  } finally {
    searching.value = false
  }
}

function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.search-page { min-height: 100vh; background: $bg-primary; }
.search-bar {
  display: flex;
  align-items: center;
  padding: 0 $spacing-md;
  gap: $spacing-sm;
  background: $bg-secondary;
  padding-bottom: $spacing-sm;
}
.search-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background: $bg-tertiary;
  border-radius: 40rpx;
  padding: 0 $spacing-md;
  height: 64rpx;
}
.search-icon { font-size: 28rpx; margin-right: $spacing-sm; }
.search-input { flex: 1; font-size: 28rpx; color: $text-primary; height: 64rpx; }
.clear-btn { font-size: 24rpx; color: $text-tertiary; padding: 8rpx; }
.cancel-btn { font-size: $font-sm; color: $accent-primary; white-space: nowrap; }
.suggestions { padding: $spacing-md; }
.suggestion-section { margin-bottom: $spacing-lg; }
.suggestion-title { font-size: $font-md; font-weight: 600; color: $text-primary; display: block; margin-bottom: $spacing-sm; }
.genre-grid { display: flex; flex-wrap: wrap; gap: $spacing-sm; }
.genre-chip {
  display: flex; align-items: center; gap: 8rpx;
  padding: 12rpx 24rpx; background: $bg-card; border-radius: 36rpx;
  font-size: $font-sm; color: $text-secondary; border: 1rpx solid $border-light;
}
.no-recent { font-size: $font-sm; color: $text-tertiary; }
.recent-item {
  display: flex; align-items: center; gap: $spacing-sm;
  padding: $spacing-sm 0; font-size: $font-sm; color: $text-secondary;
  border-bottom: 1rpx solid $border-light;
}
.remove-btn { margin-left: auto; color: $text-tertiary; padding: 8rpx; }
.loading-wrap { padding-top: 200rpx; }
.result-header { padding: $spacing-sm $spacing-lg; font-size: $font-sm; color: $text-tertiary; }
</style>
