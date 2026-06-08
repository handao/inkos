<template>
  <view class="services">
    <PageNav title="LLM 服务配置">
      <template #right>
        <text class="add-btn" @tap="showAdd = true">+ 添加</text>
      </template>
    </PageNav>

    <view v-if="loading" class="loading-wrap">
      <LoadingSpinner tip="加载服务列表..." />
    </view>

    <view v-else-if="services.length === 0" class="empty-wrap">
      <EmptyState
        icon="🔌"
        title="暂无 LLM 服务"
        desc="添加 AI 模型服务以开始创作"
        actionText="添加服务"
        @action="showAdd = true"
      />
    </view>

    <view v-else class="service-list">
      <view class="service-card" v-for="s in services" :key="s.id">
        <view class="service-header">
          <text class="service-name">{{ s.name }}</text>
          <text class="service-status" :class="{ enabled: s.enabled }">
            {{ s.enabled ? '已启用' : '已禁用' }}
          </text>
        </view>
        <view class="service-info">
          <text class="service-detail">{{ s.provider }} / {{ s.model }}</text>
        </view>
        <view class="service-actions">
          <text class="delete-btn" @tap="handleDelete(s.id)">删除</text>
        </view>
      </view>
    </view>

    <view class="modal-mask" v-if="showAdd" @tap="showAdd = false">
      <view class="modal-content" @tap.stop>
        <text class="modal-title">添加 LLM 服务</text>

        <view class="form-group">
          <text class="label">服务名称</text>
          <input class="input" v-model="form.name" placeholder="如：我的大模型" />
        </view>

        <view class="form-group">
          <text class="label">提供商</text>
          <input class="input" v-model="form.provider" placeholder="如：openai, google, anthropic" />
        </view>

        <view class="form-group">
          <text class="label">模型</text>
          <input class="input" v-model="form.model" placeholder="如：gpt-4o, gemini-2.0-flash" />
        </view>

        <view class="form-group">
          <text class="label">API 地址</text>
          <input class="input" v-model="form.baseUrl" placeholder="可选，默认使用官方 API" />
        </view>

        <view class="form-group">
          <text class="label">API Key</text>
          <input class="input" v-model="form.apiKey" type="password" password placeholder="输入 API Key" />
        </view>

        <view class="modal-actions">
          <view class="modal-btn cancel" @tap="showAdd = false">取消</view>
          <view class="modal-btn confirm" @tap="handleAdd" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</view>
        </view>
      </view>
    </view>

    <view class="safe-area-bottom" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { api, type LlmService } from '@/api'

const services = ref<LlmService[]>([])
const loading = ref(false)
const showAdd = ref(false)
const saving = ref(false)

const form = ref({
  name: '',
  provider: '',
  model: '',
  baseUrl: '',
  apiKey: '',
})

onShow(() => {
  loadServices()
})

async function loadServices() {
  loading.value = true
  try {
    services.value = await api.llm.listServices()
  } catch {} finally {
    loading.value = false
  }
}

async function handleAdd() {
  if (!form.value.name || !form.value.provider || !form.value.model) {
    uni.showToast({ title: '请填写必要信息', icon: 'none' })
    return
  }
  saving.value = true
  try {
    await api.llm.saveService({
      name: form.value.name,
      provider: form.value.provider,
      model: form.value.model,
      baseUrl: form.value.baseUrl || undefined,
    })
    if (form.value.apiKey) {
      const newServices = await api.llm.listServices()
      const created = newServices.find(s => s.name === form.value.name)
      if (created) {
        await api.llm.saveSecret({ serviceId: created.id, apiKey: form.value.apiKey })
      }
    }
    uni.showToast({ title: '添加成功', icon: 'success' })
    showAdd.value = false
    form.value = { name: '', provider: '', model: '', baseUrl: '', apiKey: '' }
    await loadServices()
  } catch (e: any) {
    uni.showToast({ title: e.message || '添加失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: string) {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除此 LLM 服务吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await api.llm.deleteService(id)
          uni.showToast({ title: '已删除', icon: 'success' })
          await loadServices()
        } catch (e: any) {
          uni.showToast({ title: e.message || '删除失败', icon: 'none' })
        }
      }
    },
  })
}
</script>

<style lang="scss" scoped>
.services {
  min-height: 100vh;
  background: $bg-primary;
}
.add-btn {
  font-size: $font-sm;
  color: $accent-primary;
}
.loading-wrap { padding-top: 200rpx; }
.empty-wrap { padding-top: 100rpx; }
.service-list { padding: $spacing-md; }
.service-card {
  background: $bg-card;
  border-radius: $radius-md;
  padding: $spacing-md;
  margin-bottom: $spacing-md;
  box-shadow: $shadow-sm;
}
.service-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.service-name { font-size: $font-base; font-weight: 600; color: $text-primary; }
.service-status {
  font-size: 20rpx;
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
  background: $bg-tertiary;
  color: $text-tertiary;
  &.enabled { background: rgba(16, 185, 129, 0.1); color: $accent-success; }
}
.service-info { margin-top: $spacing-xs; }
.service-detail { font-size: $font-sm; color: $text-tertiary; }
.service-actions { margin-top: $spacing-sm; }
.delete-btn { font-size: $font-sm; color: $accent-danger; }
.modal-mask {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: flex-end; z-index: 1000;
}
.modal-content {
  width: 100%; background: $bg-secondary;
  border-radius: $radius-xl $radius-xl 0 0;
  padding: $spacing-xl $spacing-lg calc($spacing-xl + $safe-bottom);
  max-height: 80vh;
  overflow-y: auto;
}
.modal-title { font-size: $font-lg; font-weight: 600; color: $text-primary; display: block; margin-bottom: $spacing-md; }
.form-group { margin-bottom: $spacing-md; }
.label { font-size: $font-sm; color: $text-secondary; display: block; margin-bottom: $spacing-xs; font-weight: 500; }
.input {
  width: 100%; height: 72rpx; background: $bg-primary; border-radius: $radius-sm;
  padding: 0 $spacing-sm; font-size: $font-sm; color: $text-primary; border: 2rpx solid $border-light;
}
.modal-actions { display: flex; gap: $spacing-md; margin-top: $spacing-lg; }
.modal-btn {
  flex: 1; text-align: center; padding: $spacing-sm; border-radius: $radius-md;
  font-size: $font-base; font-weight: 500;
  &.cancel { background: $bg-tertiary; color: $text-secondary; }
  &.confirm { background: $accent-primary; color: #fff; }
  &[disabled] { opacity: 0.5; }
}
</style>
