import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api, type Book, type Chapter } from '@/api'

export const useLibraryStore = defineStore('library', () => {
  const books = ref<Book[]>([])
  const currentBook = ref<Book | null>(null)
  const chapters = ref<Chapter[]>([])
  const currentChapter = ref<Chapter | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const ongoingBooks = computed(() =>
    books.value.filter(b => b.status === 'ongoing')
  )
  const draftBooks = computed(() =>
    books.value.filter(b => b.status === 'draft')
  )
  const completedBooks = computed(() =>
    books.value.filter(b => b.status === 'completed')
  )

  async function fetchBooks(force = false) {
    if (books.value.length > 0 && !force) return
    loading.value = true
    error.value = null
    try {
      const result = await api.books.list()
      books.value = result.content
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function fetchBook(id: string) {
    loading.value = true
    error.value = null
    try {
      currentBook.value = await api.books.get(id)
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function fetchChapters(bookId: string) {
    loading.value = true
    error.value = null
    try {
      chapters.value = await api.chapters.list(bookId)
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function fetchChapter(bookId: string, chapterId: string) {
    loading.value = true
    error.value = null
    try {
      currentChapter.value = await api.chapters.get(bookId, chapterId)
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function createBook(data: { title: string; genre: string; description?: string }) {
    loading.value = true
    error.value = null
    try {
      const book = await api.books.create(data)
      books.value.unshift(book)
      return book
    } catch (e: any) {
      error.value = e.message
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteBook(id: string) {
    try {
      await api.books.delete(id)
      books.value = books.value.filter(b => b.id !== id)
    } catch (e: any) {
      error.value = e.message
      throw e
    }
  }

  function clearCurrent() {
    currentBook.value = null
    chapters.value = []
    currentChapter.value = null
  }

  return {
    books, currentBook, chapters, currentChapter, loading, error,
    ongoingBooks, draftBooks, completedBooks,
    fetchBooks, fetchBook, fetchChapters, fetchChapter,
    createBook, deleteBook, clearCurrent,
  }
})
