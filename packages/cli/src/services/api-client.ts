interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

interface AuthResponseData {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserInfoData;
}

interface UserInfoData {
  id: number;
  email: string;
  nickname: string;
  avatarUrl: string | null;
  role: string;
}

interface BookData {
  id: string;
  title: string;
  genre: string;
  status: string;
  language: string;
  chaptersWritten: number;
  createdAt: string;
  updatedAt: string;
}

interface BookDetailData extends BookData {
  fanficMode: string | null;
  outline: string | null;
  coverImageUrl: string | null;
  chapters: ChapterData[];
}

interface ChapterData {
  id: number;
  bookId: string;
  chapterNumber: number;
  title: string;
  content: string;
  wordCount: number;
  status: string;
  version: number;
  createdAt: string;
  updatedAt: string;
}

interface SessionData {
  sessionId: string;
  bookId: string;
  title: string;
  mode: string;
  isDraft: boolean;
  isStreaming: boolean;
  createdAt: string;
  updatedAt: string;
}

interface SessionDetailData extends SessionData {
  messages: MessageData[];
}

interface MessageData {
  id: number;
  sessionId: string;
  role: string;
  content: string;
  toolCalls: string | null;
  sortOrder: number;
  createdAt: string;
}

interface LlmServiceData {
  id: number;
  serviceType: string;
  label: string;
  baseUrl: string;
  apiType: string;
  models: string;
  defaultModel: string;
  isCoverProvider: boolean;
  isDefault: boolean;
}

interface AdminUserData {
  id: number;
  email: string;
  nickname: string;
  role: string;
  status: string;
  createdAt: string;
}

export class ApiError extends Error {
  code: number;
  status: number;

  constructor(message: string, code: number, status: number) {
    super(message);
    this.name = 'ApiError';
    this.code = code;
    this.status = status;
  }
}

export class ApiClient {
  private baseUrl: string;
  private timeout: number;
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private refreshing: Promise<boolean> | null = null;

  constructor(baseUrl: string = 'http://localhost:8080', timeout: number = 30000) {
    this.baseUrl = `${baseUrl.replace(/\/+$/, '')}/api/v1`;
    this.timeout = timeout;
  }

  setTokens(accessToken: string, refreshToken: string): void {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  clearTokens(): void {
    this.accessToken = null;
    this.refreshToken = null;
  }

  isAuthenticated(): boolean {
    return this.accessToken !== null;
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  private async fetchRaw<T>(
    method: string,
    path: string,
    body?: unknown,
  ): Promise<{ ok: boolean; status: number; json: ApiResponse<T> }> {
    const url = `${this.baseUrl}${path}`;
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    if (this.accessToken) {
      headers['Authorization'] = `Bearer ${this.accessToken}`;
    }

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.timeout);

    try {
      const response = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
        signal: controller.signal,
      });
      clearTimeout(timeoutId);

      const json = await response.json() as ApiResponse<T>;
      return { ok: response.ok && json.code === 200, status: response.status, json };
    } catch (e) {
      clearTimeout(timeoutId);
      if (e instanceof Error && e.name === 'AbortError') {
        throw new Error('Request timed out');
      }
      throw e;
    }
  }

  private async request<T>(
    method: string,
    path: string,
    body?: unknown,
    retried = false,
  ): Promise<T> {
    const { ok, status, json } = await this.fetchRaw<T>(method, path, body);

    if (!ok) {
      if (status === 401 && !retried && this.refreshToken) {
        const refreshed = await this.handleRefresh();
        if (refreshed) {
          return this.request<T>(method, path, body, true);
        }
      }
      throw new ApiError(json.message || `Request failed`, json.code, status);
    }

    return json.data;
  }

  private async handleRefresh(): Promise<boolean> {
    if (this.refreshing) {
      return this.refreshing;
    }

    this.refreshing = this.doRefresh();
    try {
      return await this.refreshing;
    } finally {
      this.refreshing = null;
    }
  }

  private async doRefresh(): Promise<boolean> {
    const rt = this.refreshToken;
    if (!rt) return false;

    const url = `${this.baseUrl}/auth/refresh`;
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.timeout);

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: rt }),
        signal: controller.signal,
      });
      clearTimeout(timeoutId);

      if (!response.ok) {
        this.clearTokens();
        return false;
      }

      const json = await response.json() as ApiResponse<AuthResponseData>;
      if (json.code !== 200 || !json.data) {
        this.clearTokens();
        return false;
      }

      this.accessToken = json.data.accessToken;
      this.refreshToken = json.data.refreshToken;
      return true;
    } catch {
      clearTimeout(timeoutId);
      this.clearTokens();
      return false;
    }
  }

  // ============ Auth ============

  async login(email: string, password: string): Promise<{ accessToken: string; refreshToken: string; user: UserInfoData }> {
    const data = await this.request<AuthResponseData>('POST', '/auth/login', { email, password });
    this.accessToken = data.accessToken;
    this.refreshToken = data.refreshToken;
    return { accessToken: data.accessToken, refreshToken: data.refreshToken, user: data.user };
  }

  async register(email: string, code: string, password: string, nickname: string): Promise<{ accessToken: string; refreshToken: string; user: UserInfoData }> {
    const data = await this.request<AuthResponseData>('POST', '/auth/register', { email, code, password, nickname });
    this.accessToken = data.accessToken;
    this.refreshToken = data.refreshToken;
    return { accessToken: data.accessToken, refreshToken: data.refreshToken, user: data.user };
  }

  async sendCode(email: string): Promise<void> {
    await this.request<void>('POST', '/auth/send-code', { email });
  }

  async getMe(): Promise<UserInfoData> {
    return this.request<UserInfoData>('GET', '/auth/me');
  }

  async refresh(token: string): Promise<{ accessToken: string; refreshToken: string; user: UserInfoData }> {
    const data = await this.request<AuthResponseData>('POST', '/auth/refresh', { refreshToken: token });
    this.accessToken = data.accessToken;
    this.refreshToken = data.refreshToken;
    return { accessToken: data.accessToken, refreshToken: data.refreshToken, user: data.user };
  }

  // ============ Books ============

  async listBooks(params?: { status?: string; page?: number; size?: number }): Promise<{ content: BookData[]; total: number }> {
    const query = new URLSearchParams();
    if (params?.status) query.set('status', params.status);
    if (params?.page !== undefined) query.set('page', String(params.page));
    if (params?.size !== undefined) query.set('size', String(params.size));
    const qs = query.toString();
    const data = await this.request<PagedResponse<BookData>>('GET', `/books${qs ? `?${qs}` : ''}`);
    return { content: data.content, total: data.total };
  }

  async getBook(id: string): Promise<BookDetailData> {
    return this.request<BookDetailData>('GET', `/books/${encodeURIComponent(id)}`);
  }

  async createBook(data: { title: string; genre?: string; language?: string; outline?: string }): Promise<BookData> {
    return this.request<BookData>('POST', '/books', data);
  }

  async updateBook(id: string, data: { title?: string; genre?: string; status?: string; outline?: string }): Promise<BookData> {
    return this.request<BookData>('PUT', `/books/${encodeURIComponent(id)}`, data);
  }

  async deleteBook(id: string): Promise<void> {
    await this.request<void>('DELETE', `/books/${encodeURIComponent(id)}`);
  }

  // ============ Chapters ============

  async listChapters(bookId: string): Promise<ChapterData[]> {
    return this.request<ChapterData[]>('GET', `/books/${encodeURIComponent(bookId)}/chapters`);
  }

  async getChapter(id: number): Promise<ChapterData> {
    return this.request<ChapterData>('GET', `/chapters/${id}`);
  }

  async createChapter(bookId: string, data: { title: string; content?: string; chapterNumber?: number }): Promise<ChapterData> {
    return this.request<ChapterData>('POST', `/books/${encodeURIComponent(bookId)}/chapters`, data);
  }

  async updateChapter(id: number, data: { title?: string; content?: string; chapterNumber?: number }): Promise<ChapterData> {
    return this.request<ChapterData>('PUT', `/chapters/${id}`, data);
  }

  async deleteChapter(id: number): Promise<void> {
    await this.request<void>('DELETE', `/chapters/${id}`);
  }

  // ============ Sessions (AI writing) ============

  async listSessions(bookId?: string): Promise<SessionData[]> {
    const qs = bookId ? `?bookId=${encodeURIComponent(bookId)}` : '';
    return this.request<SessionData[]>('GET', `/sessions${qs}`);
  }

  async createSession(bookId: string, title?: string, mode?: string): Promise<SessionData> {
    return this.request<SessionData>('POST', '/sessions', { bookId, title, mode });
  }

  async getSession(id: string): Promise<SessionDetailData> {
    return this.request<SessionDetailData>('GET', `/sessions/${encodeURIComponent(id)}`);
  }

  async updateSession(id: string, data: { title?: string; mode?: string }): Promise<SessionData> {
    return this.request<SessionData>('PUT', `/sessions/${encodeURIComponent(id)}`, data);
  }

  async deleteSession(id: string): Promise<void> {
    await this.request<void>('DELETE', `/sessions/${encodeURIComponent(id)}`);
  }

  async sendMessage(sessionId: string, content: string): Promise<MessageData> {
    return this.request<MessageData>('POST', `/sessions/${encodeURIComponent(sessionId)}/messages`, { content });
  }

  // ============ LLM Services ============

  async listServices(): Promise<LlmServiceData[]> {
    return this.request<LlmServiceData[]>('GET', '/llm/services');
  }

  async saveService(data: { serviceType: string; label?: string; baseUrl?: string; apiType?: string; models?: string[]; defaultModel?: string; isCoverProvider?: boolean }): Promise<LlmServiceData> {
    return this.request<LlmServiceData>('POST', '/llm/services', data);
  }

  async deleteService(id: number): Promise<void> {
    await this.request<void>('DELETE', `/llm/services/${id}`);
  }

  async checkSecret(serviceKey: string): Promise<boolean> {
    const data = await this.request<{ hasKey: boolean }>('GET', `/llm/secrets/${encodeURIComponent(serviceKey)}`);
    return data.hasKey;
  }

  async saveSecret(serviceKey: string, apiKey: string): Promise<void> {
    await this.request<void>('PUT', `/llm/secrets/${encodeURIComponent(serviceKey)}`, { apiKey });
  }

  // ============ Admin ============

  async listUsers(page?: number, size?: number): Promise<{ content: AdminUserData[]; total: number }> {
    const query = new URLSearchParams();
    if (page !== undefined) query.set('page', String(page));
    if (size !== undefined) query.set('size', String(size));
    const qs = query.toString();
    const data = await this.request<{ content: AdminUserData[]; total: number }>('GET', `/admin/users${qs ? `?${qs}` : ''}`);
    return data;
  }

  async updateUserStatus(userId: number, status: string): Promise<void> {
    await this.request<void>('PUT', `/admin/users/${userId}/status`, { status });
  }
}

export const api = new ApiClient();
