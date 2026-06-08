<template>
  <view class="create-page">
    <PageNav title="新建作品" />

    <view class="form">
      <view class="form-section">
        <text class="form-label">作品名称 <text class="required">*</text></text>
        <input
          class="form-input"
          v-model="title"
          placeholder="给你的小说起个名字"
          :maxlength="50"
        />
        <text class="char-count">{{ title.length }}/50</text>
      </view>

      <view class="form-section">
        <text class="form-label">选择题材 <text class="required">*</text></text>
        <GenrePicker v-model="genre" />
      </view>

      <view class="form-section">
        <text class="form-label">作品简介</text>
        <textarea
          class="form-textarea"
          v-model="description"
          placeholder="用几句话介绍你的作品..."
          :maxlength="500"
        />
        <text class="char-count">{{ description.length }}/500</text>
      </view>

      <view class="form-section">
        <text class="form-label">创作风格</text>
        <view class="style-options">
          <view
            v-for="s in styles"
            :key="s.value"
            class="style-chip"
            :class="{ active: style === s.value }"
            @tap="style = s.value"
          >
            {{ s.label }}
          </view>
        </view>
      </view>

      <view class="form-section" v-if="genre === 'fanfic'">
        <text class="form-label">原作名称</text>
        <input
          class="form-input"
          v-model="sourceWork"
          placeholder="如同名动漫、小说、游戏等"
        />
      </view>

      <view class="form-section">
        <text class="form-label">目标字数</text>
        <view class="word-count-options">
          <view
            v-for="w in wordCountOptions"
            :key="w.value"
            class="word-chip"
            :class="{ active: targetWords === w.value }"
            @tap="targetWords = w.value"
          >
            {{ w.label }}
          </view>
        </view>
      </view>

      <view class="form-section">
        <text class="form-label">AI 模型</text>
        <view class="model-picker">
          <picker :value="modelIndex" :range="modelOptions" @change="onModelChange">
            <view class="picker-trigger">
              <text>{{ modelOptions[modelIndex] || '选择模型' }}</text>
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>
      </view>

      <view class="create-btn-wrap">
        <view
          class="create-btn"
          :class="{ disabled: !canCreate }"
          @tap="handleCreate"
        >
          {{ creating ? '创建中...' : '开始创作' }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useLibraryStore } from '@/stores/library'
import { useAppStore } from '@/stores/app'

const libraryStore = useLibraryStore()
const appStore = useAppStore()

const title = ref('')
const genre = ref('fantasy')
const description = ref('')
const style = ref('standard')
const sourceWork = ref('')
const targetWords = ref(50000)
const creating = ref(false)
const modelIndex = ref(0)

const modelOptions = [
  '默认模型', 'GPT-4o', 'Claude 3.5 Sonnet', 'Kimi K2.5', 'Gemini 2.0 Flash',
]

const styles = [
  { value: 'standard', label: '标准' },
  { value: 'detailed', label: '细腻描写' },
  { value: 'fast', label: '简洁明快' },
  { value: 'literary', label: '文学风格' },
  { value: 'dialogue', label: '对话驱动' },
]

const wordCountOptions = [
  { value: 20000, label: '短篇 (2万)' },
  { value: 50000, label: '中篇 (5万)' },
  { value: 100000, label: '长篇 (10万)' },
  { value: 300000, label: '超长篇 (30万)' },
]

const canCreate = computed(() => title.value.trim().length > 0 && genre.value)

function onModelChange(e: any) {
  modelIndex.value = e.detail.value
}

async function handleCreate() {
  if (!canCreate.value) {
    appStore.showToast('请填写作品名称和选择题材', 'none')
    return
  }
  if (creating.value) return
  creating.value = true
  try {
    const book = await libraryStore.createBook({
      title: title.value.trim(),
      genre: genre.value,
      description: description.value.trim() || `${styles.find(s => s.value === style)?.label || ''}${genre}题材小说`,
    })
    appStore.showToast('作品创建成功', 'success')
    uni.redirectTo({ url: `/pages/library/detail?id=${book.id}` })
  } catch (e: any) {
    appStore.showToast(e.message, 'error')
  } finally {
    creating.value = false
  }
}
</script>

<style lang="scss" scoped>
.create-page { min-height: 100vh; background: $bg-primary; }
.form { padding: $spacing-lg; }
.form-section {
  margin-bottom: $spacing-xl;
  position: relative;
}
.form-label {
  font-size: $font-sm;
  color: $text-secondary;
  display: block;
  margin-bottom: $spacing-sm;
  font-weight: 500;
}
.required { color: $accent-danger; }
.form-input {
  width: 100%;
  background: $bg-card;
  border-radius: $radius-sm;
  padding: $spacing-sm $spacing-md;
  font-size: $font-base;
  color: $text-primary;
  border: 2rpx solid $border-light;
  height: 72rpx;
}
.form-textarea {
  width: 100%;
  background: $bg-card;
  border-radius: $radius-sm;
  padding: $spacing-sm $spacing-md;
  font-size: $font-base;
  color: $text-primary;
  border: 2rpx solid $border-light;
  height: 200rpx;
}
.char-count {
  position: absolute;
  right: 0;
  top: 0;
  font-size: 22rpx;
  color: $text-tertiary;
}
.style-options {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
}
.style-chip {
  padding: 12rpx 28rpx;
  border-radius: 36rpx;
  background: $bg-card;
  border: 2rpx solid $border-light;
  font-size: $font-sm;
  color: $text-secondary;
  &.active {
    border-color: $accent-primary;
    color: $accent-primary;
    background: rgba(59, 130, 246, 0.06);
  }
}
.word-count-options {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
}
.word-chip {
  padding: 12rpx 28rpx;
  border-radius: 36rpx;
  background: $bg-card;
  border: 2rpx solid $border-light;
  font-size: $font-sm;
  color: $text-secondary;
  &.active {
    border-color: $accent-primary;
    color: $accent-primary;
    background: rgba(59, 130, 246, 0.06);
  }
}
.model-picker {
  background: $bg-card;
  border-radius: $radius-sm;
  border: 2rpx solid $border-light;
}
.picker-trigger {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-sm $spacing-md;
  font-size: $font-base;
  color: $text-primary;
}
.picker-arrow { font-size: 24rpx; color: $text-tertiary; }
.create-btn-wrap { margin-top: $spacing-xl; padding-bottom: $spacing-xl; }
.create-btn {
  width: 100%;
  text-align: center;
  padding: $spacing-md;
  border-radius: $radius-md;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: #fff;
  font-size: $font-md;
  font-weight: 600;
  &.disabled { opacity: 0.5; }
  &:active:not(.disabled) { opacity: 0.8; }
}
</style>
