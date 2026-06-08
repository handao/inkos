const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

function getToken(): string | null {
  try {
    return uni.getStorageSync('accessToken') || null
  } catch { return null }
}

function setToken(token: string) {
  uni.setStorageSync('accessToken', token)
}

function getRefreshToken(): string | null {
  try {
    return uni.getStorageSync('refreshToken') || null
  } catch { return null }
}

function setRefreshToken(token: string) {
  uni.setStorageSync('refreshToken', token)
}

function clearTokens() {
  try {
    uni.removeStorageSync('accessToken')
    uni.removeStorageSync('refreshToken')
  } catch {}
}

export interface Book {
  id: string
  title: string
  genre: string
  status: 'draft' | 'ongoing' | 'completed'
  language: string
  fanficMode: string | null
  chaptersWritten: number
  outline: string | null
  coverImageUrl: string | null
  createdAt: string
  updatedAt: string
}

export interface Chapter {
  id: string
  bookId: string
  title: string
  content: string
  wordCount: number
  chapterNumber: number
  status: 'draft' | 'generated' | 'revised' | 'published'
  createdAt: string
  updatedAt: string
}

export interface UserProfile {
  id: string
  email: string
  nickname: string
  role: 'user' | 'admin'
  avatarUrl: string | null
  createdAt: string
  updatedAt: string
}

export interface Session {
  id: string
  title: string
  bookId: string | null
  createdAt: string
  updatedAt: string
}

export interface Message {
  id: string
  sessionId: string
  role: 'user' | 'assistant' | 'system'
  content: string
  createdAt: string
}

export interface LlmService {
  id: string
  name: string
  provider: string
  model: string
  baseUrl: string
  enabled: boolean
  createdAt: string
  updatedAt: string
}

export interface AdminUser {
  id: string
  email: string
  nickname: string
  role: 'user' | 'admin'
  status: 'active' | 'disabled'
  createdAt: string
  lastLoginAt?: string
}

export interface Novel {
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

export interface AllowedEmail {
  email: string
  note: string | null
  createdAt: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  user: UserProfile
}

export interface RegisterRequest {
  email: string
  password: string
  nickname: string
  code: string
}

export interface SendCodeRequest {
  email: string
}

export interface RefreshRequest {
  refreshToken: string
}

export interface CreateBookRequest {
  title: string
  genre: string
  description?: string
}

export interface UpdateBookRequest {
  title?: string
  genre?: string
  status?: string
  outline?: string
}

export interface CreateChapterRequest {
  title: string
  content?: string
}

export interface UpdateChapterRequest {
  title?: string
  content?: string
  status?: string
}

export interface SendMessageRequest {
  content: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

function handleResponse<T>(res: any): T {
  if (res.statusCode >= 200 && res.statusCode < 300) {
    const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
    if (data.error) throw new Error(data.error)
    return data.data ?? data
  }
  if (res.statusCode === 401) {
    clearTokens()
    uni.reLaunch({ url: '/pages/auth/login' })
  }
  throw new Error(`API error: ${res.statusCode}`)
}

function handleError(err: any): never {
  const msg = err?.errMsg || err?.message || 'Unknown error'
  uni.showToast({ title: msg, icon: 'none' })
  throw new Error(msg)
}

async function tryRefreshToken(): Promise<boolean> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) return false
  try {
    const res = await uni.request({
      url: `${BASE_URL}/auth/refresh`,
      method: 'POST',
      data: { refreshToken },
      header: { 'Content-Type': 'application/json' },
    })
    if (res.statusCode >= 200 && res.statusCode < 300) {
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
      const result = data.data ?? data
      if (result.accessToken) {
        setToken(result.accessToken)
        if (result.refreshToken) setRefreshToken(result.refreshToken)
        return true
      }
    }
  } catch {}
  clearTokens()
  uni.reLaunch({ url: '/pages/auth/login' })
  return false
}

export const api = {
  async request<T>(method: string, path: string, data?: any, authenticated = true): Promise<T> {
    const header: Record<string, string> = { 'Content-Type': 'application/json' }
    if (authenticated) {
      const token = getToken()
      if (token) header['Authorization'] = `Bearer ${token}`
    }
    try {
      const res = await uni.request({
        url: `${BASE_URL}${path}`,
        method,
        data,
        header,
      })
      if (res.statusCode === 401 && authenticated) {
        const refreshed = await tryRefreshToken()
        if (refreshed) {
          const newToken = getToken()
          if (newToken) header['Authorization'] = `Bearer ${newToken}`
          const retry = await uni.request({
            url: `${BASE_URL}${path}`,
            method,
            data,
            header,
          })
          return handleResponse<T>(retry)
        }
      }
      return handleResponse<T>(res)
    } catch (err) {
      return handleError(err)
    }
  },

  auth: {
    sendCode: (data: SendCodeRequest) =>
      api.request<{ success: boolean }>('POST', '/auth/send-code', data, false),

    register: (data: RegisterRequest) =>
      api.request<LoginResponse>('POST', '/auth/register', data, false),

    login: (data: LoginRequest) =>
      api.request<LoginResponse>('POST', '/auth/login', data, false),

    refresh: (data: RefreshRequest) =>
      api.request<LoginResponse>('POST', '/auth/refresh', data, false),
  },

  users: {
    getMe: () =>
      api.request<UserProfile>('GET', '/users/me'),
  },

  books: {
    list: (params?: { page?: number; size?: number; genre?: string; status?: string }) =>
      api.request<PageResponse<Book>>('GET', '/books', params),

    get: (id: string) =>
      api.request<Book>('GET', `/books/${id}`),

    create: (data: CreateBookRequest) =>
      api.request<Book>('POST', '/books', data),

    update: (id: string, data: UpdateBookRequest) =>
      api.request<Book>('PUT', `/books/${id}`, data),

    delete: (id: string) =>
      api.request<void>('DELETE', `/books/${id}`),
  },

  search: (query: string) =>
    api.request<Novel[]>('GET', '/books/search', { q: query }),

  chapters: {
    list: (bookId: string) =>
      api.request<Chapter[]>('GET', `/books/${bookId}/chapters`),

    get: (bookId: string, chapterId: string) =>
      api.request<Chapter>('GET', `/books/${bookId}/chapters/${chapterId}`),

    create: (bookId: string, data: CreateChapterRequest) =>
      api.request<Chapter>('POST', `/books/${bookId}/chapters`, data),

    update: (bookId: string, chapterId: string, data: UpdateChapterRequest) =>
      api.request<Chapter>('PUT', `/books/${bookId}/chapters/${chapterId}`, data),

    delete: (bookId: string, chapterId: string) =>
      api.request<void>('DELETE', `/books/${bookId}/chapters/${chapterId}`),
  },

  sessions: {
    list: () =>
      api.request<Session[]>('GET', '/sessions'),

    get: (id: string) =>
      api.request<Session>('GET', `/sessions/${id}`),

    create: (data: { title?: string; bookId?: string }) =>
      api.request<Session>('POST', '/sessions', data),

    delete: (id: string) =>
      api.request<void>('DELETE', `/sessions/${id}`),

    sendMessage: (sessionId: string, data: SendMessageRequest) =>
      api.request<Message>('POST', `/sessions/${sessionId}/messages`, data),

    getMessages: (sessionId: string) =>
      api.request<Message[]>('GET', `/sessions/${sessionId}/messages`),
  },

  llm: {
    listServices: () =>
      api.request<LlmService[]>('GET', '/llm/services'),

    saveService: (data: { name: string; provider: string; model: string; baseUrl?: string }) =>
      api.request<LlmService>('POST', '/llm/services', data),

    deleteService: (id: string) =>
      api.request<void>('DELETE', `/llm/services/${id}`),

    saveSecret: (data: { serviceId: string; apiKey: string }) =>
      api.request<void>('POST', '/llm/secrets', data),
  },

  admin: {
    listUsers: (params?: { keyword?: string; page?: number; size?: number }) =>
      api.request<AdminUser[]>('GET', '/admin/users', params),

    updateUserStatus: (id: string, status: 'active' | 'disabled') =>
      api.request<void>('PUT', `/admin/users/${id}/status`, { status }),

    listAllowedEmails: () =>
      api.request<AllowedEmail[]>('GET', '/admin/allowed-emails'),

    addAllowedEmail: (data: { email: string; note?: string }) =>
      api.request<AllowedEmail>('POST', '/admin/allowed-emails', data),

    deleteAllowedEmail: (email: string) =>
      api.request<void>('DELETE', `/admin/allowed-emails/${encodeURIComponent(email)}`),

    listWhitelist: () =>
      api.request<AllowedEmail[]>('GET', '/admin/allowed-emails'),

    addToWhitelist: (email: string) =>
      api.request<AllowedEmail>('POST', '/admin/allowed-emails', { email }),

    removeFromWhitelist: (id: number) =>
      api.request<void>('DELETE', `/admin/allowed-emails/${id}`),
  },
}

export function formatWordCount(count: number): string {
  if (count >= 10000) return `${(count / 10000).toFixed(1)}万`
  if (count >= 1000) return `${(count / 1000).toFixed(1)}k`
  return String(count)
}

export function formatDate(dateStr: string): string {
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
