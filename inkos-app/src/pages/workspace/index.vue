<template>
  <view class="workspace">
    <PageNav :title="currentSession?.title || 'AI 写作工作台'">
      <template #right>
        <text class="session-btn" @tap="showSessions = true">📋</text>
      </template>
    </PageNav>

    <view class="chat-container">
      <scroll-view
        class="message-list"
        scroll-y
        :scroll-into-view="scrollTarget"
        scroll-with-animation
      >
        <view v-if="messages.length === 0" class="welcome">
          <text class="welcome-icon">🤖</text>
          <text class="welcome-title">欢迎来到 AI 写作工作台</text>
          <text class="welcome-desc">在这里与 AI 对话，生成小说章节、讨论情节、完善设定</text>
          <view class="suggestion-chips" v-if="!currentSessionId">
            <view class="chip" @tap="startNewSession">开始新的写作会话</view>
          </view>
        </view>

        <view
          v-for="(msg, i) in messages"
          :key="msg.id || i"
          :id="'msg-' + (msg.id || i)"
          class="message"
          :class="msg.role"
        >
          <view class="bubble">
            <text>{{ msg.content }}</text>
          </view>
        </view>

        <view v-if="sending" class="message assistant">
          <view class="bubble typing">
            <text class="typing-dot">.</text>
            <text class="typing-dot">.</text>
            <text class="typing-dot">.</text>
          </view>
        </view>

        <view class="safe-area-bottom" />
      </scroll-view>

      <view class="input-bar">
        <input
          class="input-field"
          v-model="inputText"
          placeholder="输入你的写作需求..."
          :disabled="sending || !currentSessionId"
          confirm-type="send"
          @confirm="sendMessage"
        />
        <view
          class="send-btn"
          :class="{ disabled: !canSend }"
          @tap="sendMessage"
        >
          发送
        </view>
      </view>
    </view>

    <view class="modal-mask" v-if="showSessions" @tap="showSessions = false">
      <view class="sessions-panel" @tap.stop>
        <text class="panel-title">会话列表</text>
        <scroll-view class="session-scroll" scroll-y>
          <view
            v-for="s in sessions"
            :key="s.id"
            class="session-item"
            :class="{ active: s.id === currentSessionId }"
            @tap="switchSession(s.id)"
          >
            <text class="session-name">{{ s.title || '未命名会话' }}</text>
            <text class="session-time">{{ formatDate(s.updatedAt) }}</text>
          </view>
          <view v-if="sessions.length === 0" class="no-sessions">
            <text>暂无会话</text>
          </view>
        </scroll-view>
        <view class="panel-actions">
          <view class="panel-btn" @tap="startNewSession">新建会话</view>
          <view class="panel-btn cancel" @tap="showSessions = false">关闭</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { api, type Session, type Message, formatDate } from '@/api'

const currentSessionId = ref('')
const currentSession = ref<Session | null>(null)
const sessions = ref<Session[]>([])
const messages = ref<Message[]>([])
const inputText = ref('')
const sending = ref(false)
const loading = ref(false)
const showSessions = ref(false)
const scrollTarget = ref('')

const canSend = computed(() => inputText.value.trim().length > 0 && !sending.value && !!currentSessionId.value)

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  const bookId = page?.options?.bookId || ''
  loadSessions(bookId)
})

watch(messages, () => {
  nextTick(() => {
    if (messages.value.length > 0) {
      const last = messages.value[messages.value.length - 1]
      scrollTarget.value = 'msg-' + (last.id || messages.value.length - 1)
    }
  })
}, { deep: true })

async function loadSessions(bookId?: string) {
  try {
    sessions.value = await api.sessions.list()
    if (sessions.value.length > 0) {
      await switchSession(sessions.value[0].id)
    } else if (bookId) {
      await startNewSession(bookId)
    }
  } catch {}
}

async function switchSession(id: string) {
  currentSessionId.value = id
  showSessions.value = false
  try {
    currentSession.value = await api.sessions.get(id)
    messages.value = await api.sessions.getMessages(id)
  } catch {} finally {
    loading.value = false
  }
}

async function startNewSession(bookId?: string) {
  showSessions.value = false
  try {
    const session = await api.sessions.create({
      title: bookId ? '写作会话' : '新的写作会话',
      bookId: bookId || undefined,
    })
    sessions.value.unshift(session)
    await switchSession(session.id)
  } catch (e: any) {
    uni.showToast({ title: e.message || '创建失败', icon: 'none' })
  }
}

async function sendMessage() {
  if (!canSend.value) return
  const text = inputText.value.trim()
  inputText.value = ''
  messages.value.push({
    id: '',
    sessionId: currentSessionId.value,
    role: 'user',
    content: text,
    createdAt: new Date().toISOString(),
  })
  sending.value = true
  try {
    const reply = await api.sessions.sendMessage(currentSessionId.value, { content: text })
    messages.value.push(reply)
  } catch (e: any) {
    uni.showToast({ title: e.message || '发送失败', icon: 'none' })
  } finally {
    sending.value = false
  }
}
</script>

<style lang="scss" scoped>
.workspace {
  min-height: 100vh;
  background: $bg-primary;
  display: flex;
  flex-direction: column;
}
.session-btn {
  font-size: 36rpx;
}
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.message-list {
  flex: 1;
  padding: $spacing-md;
}
.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 40rpx 60rpx;
}
.welcome-icon { font-size: 80rpx; margin-bottom: $spacing-md; }
.welcome-title { font-size: $font-lg; font-weight: 600; color: $text-primary; text-align: center; }
.welcome-desc { font-size: $font-sm; color: $text-tertiary; text-align: center; margin-top: $spacing-sm; }
.suggestion-chips { margin-top: $spacing-lg; }
.chip {
  padding: 16rpx 40rpx;
  background: $accent-primary;
  color: #fff;
  border-radius: 40rpx;
  font-size: $font-sm;
}
.message {
  margin-bottom: $spacing-md;
  display: flex;
  &.assistant { justify-content: flex-start; }
  &.user { justify-content: flex-end; }
}
.bubble {
  max-width: 70%;
  padding: $spacing-sm $spacing-md;
  border-radius: $radius-md;
  font-size: $font-sm;
  line-height: 1.7;
  word-break: break-word;
}
.assistant .bubble {
  background: $bg-card;
  color: $text-primary;
  border: 1rpx solid $border-light;
}
.user .bubble {
  background: $accent-primary;
  color: #fff;
}
.typing {
  display: flex;
  gap: 4rpx;
  align-items: center;
  min-width: 80rpx;
  justify-content: center;
}
.typing-dot {
  animation: blink 1.4s infinite;
  font-size: 32rpx;
  line-height: 1;
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.4s; }
}
@keyframes blink {
  0%, 60%, 100% { opacity: 0.3; }
  30% { opacity: 1; }
}
.input-bar {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-sm $spacing-md;
  background: $bg-secondary;
  border-top: 1rpx solid $border-light;
  padding-bottom: calc($spacing-sm + $safe-bottom);
}
.input-field {
  flex: 1;
  height: 72rpx;
  background: $bg-primary;
  border-radius: 36rpx;
  padding: 0 $spacing-md;
  font-size: $font-sm;
  color: $text-primary;
}
.send-btn {
  padding: 0 32rpx;
  height: 72rpx;
  line-height: 72rpx;
  background: $accent-primary;
  color: #fff;
  border-radius: 36rpx;
  font-size: $font-sm;
  white-space: nowrap;
  &.disabled { opacity: 0.5; }
  &:active:not(.disabled) { opacity: 0.8; }
}
.modal-mask {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: flex-end; z-index: 1000;
}
.sessions-panel {
  width: 100%;
  background: $bg-secondary;
  border-radius: $radius-xl $radius-xl 0 0;
  padding: $spacing-xl $spacing-lg calc($spacing-xl + $safe-bottom);
  max-height: 60vh;
  display: flex;
  flex-direction: column;
}
.panel-title { font-size: $font-lg; font-weight: 600; color: $text-primary; display: block; margin-bottom: $spacing-md; }
.session-scroll { flex: 1; }
.session-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-sm 0;
  border-bottom: 1rpx solid $border-light;
  &.active { background: rgba(59, 130, 246, 0.05); margin: 0 -$spacing-sm; padding: $spacing-sm; border-radius: $radius-sm; }
}
.session-name { font-size: $font-sm; color: $text-primary; }
.session-time { font-size: $font-xs; color: $text-tertiary; }
.no-sessions { text-align: center; padding: $spacing-xl 0; font-size: $font-sm; color: $text-tertiary; }
.panel-actions { display: flex; gap: $spacing-md; margin-top: $spacing-lg; }
.panel-btn {
  flex: 1; text-align: center; padding: $spacing-sm; border-radius: $radius-md;
  background: $accent-primary; color: #fff; font-size: $font-base;
  &.cancel { background: $bg-tertiary; color: $text-secondary; }
}
</style>
