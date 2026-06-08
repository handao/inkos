import { ApiClient, ApiError } from './api-client.js';

export class BackendAdapterError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'BackendAdapterError';
  }
}

function formatErr(e: unknown): string {
  if (e instanceof ApiError) return `[${e.status}] ${e.message}`;
  if (e instanceof Error) return e.message;
  return String(e);
}

export class BackendAdapter {
  private api: ApiClient;

  constructor(apiClient?: ApiClient) {
    this.api = apiClient ?? new ApiClient();
  }

  // ============ Pipeline operations (delegated to Java pipeline endpoints) ============

  async writeNextChapter(bookId: string, _options?: { wordCount?: number; context?: string }): Promise<{ content: string; title: string; chapterNumber: number }> {
    const book = await this.api.getBook(bookId);
    const chapters = await this.api.listChapters(bookId);
    const chapterNumber = chapters.length + 1;

    const result = await this.api.createChapter(bookId, {
      chapterNumber,
      title: `Chapter ${chapterNumber}`,
      content: '',
    });

    return {
      content: result.content,
      title: result.title,
      chapterNumber: result.chapterNumber,
    };
  }

  async writeDraft(_bookId: string, _startChapter?: number, _endChapter?: number): Promise<void> {
    throw new BackendAdapterError('writeDraft: not implemented in backend mode');
  }

  async planChapter(_bookId: string, _chapterNumber: number): Promise<string> {
    throw new BackendAdapterError('planChapter: not implemented in backend mode');
  }

  async composeChapter(_bookId: string, _chapterNumber: number): Promise<unknown> {
    throw new BackendAdapterError('composeChapter: not implemented in backend mode');
  }

  async auditChapter(_bookId: string, _chapterNumber: number): Promise<{ passed: boolean; issues: string[] }> {
    throw new BackendAdapterError('auditChapter: not implemented in backend mode');
  }

  async reviseChapter(_bookId: string, _chapterNumber: number, _issues: string[]): Promise<{ content: string }> {
    throw new BackendAdapterError('reviseChapter: not implemented in backend mode');
  }

  // ============ Book management ============

  async initBook(data: { title: string; genre: string; language?: string; outline?: string }): Promise<{ id: string }> {
    const book = await this.api.createBook(data);
    return { id: book.id };
  }

  async getBookProfile(bookId: string): Promise<{
    id: string;
    title: string;
    genre: string;
    status: string;
    language: string;
    chaptersWritten: number;
    outline: string | null;
    createdAt: string;
  }> {
    const book = await this.api.getBook(bookId);
    return {
      id: book.id,
      title: book.title,
      genre: book.genre,
      status: book.status,
      language: book.language,
      chaptersWritten: book.chaptersWritten,
      outline: book.outline,
      createdAt: book.createdAt,
    };
  }

  async listAllBooks(): Promise<Array<{ id: string; title: string; genre: string; status: string; chaptersWritten: number }>> {
    const { content } = await this.api.listBooks({ size: 100 });
    return content.map(b => ({
      id: b.id,
      title: b.title,
      genre: b.genre,
      status: b.status,
      chaptersWritten: b.chaptersWritten,
    }));
  }

  // ============ LLM configuration ============

  async configureLlm(serviceName: string, config: { label?: string; baseUrl?: string; apiType?: string; models?: string[]; defaultModel?: string }): Promise<void> {
    await this.api.saveService({ serviceType: serviceName, ...config });
  }

  async listConfiguredServices(): Promise<unknown[]> {
    return this.api.listServices();
  }

  // ============ State ============

  async getRuntimeState(_bookId: string): Promise<Record<string, unknown>> {
    throw new BackendAdapterError('getRuntimeState: not implemented in backend mode');
  }

  async getPipelineState(_bookId: string): Promise<Record<string, unknown>> {
    throw new BackendAdapterError('getPipelineState: not implemented in backend mode');
  }
}
