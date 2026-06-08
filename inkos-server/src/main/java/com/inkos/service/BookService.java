package com.inkos.service;

import com.inkos.dto.request.CreateBookRequest;
import com.inkos.dto.request.UpdateBookRequest;
import com.inkos.dto.response.BookResponse;
import com.inkos.dto.response.BookDetailResponse;
import com.inkos.dto.response.ChapterResponse;
import com.inkos.entity.Book;
import com.inkos.entity.Chapter;
import com.inkos.exception.BusinessException;
import com.inkos.exception.ErrorCode;
import com.inkos.repository.BookRepository;
import com.inkos.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;

    public Page<BookResponse> listBooks(Long userId, int page, int size) {
        return bookRepository.findByUserId(userId, PageRequest.of(page, size))
                .map(BookResponse::from);
    }

    public BookDetailResponse getBook(String id, Long userId) {
        Book book = bookRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByChapterNumberAsc(id);
        return BookDetailResponse.from(book, chapters.stream().map(ChapterResponse::from).toList());
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request, Long userId) {
        Book book = Book.builder()
                .id(UUID.randomUUID().toString().toLowerCase())
                .userId(userId)
                .title(request.getTitle())
                .genre(request.getGenre())
                .language(request.getLanguage() != null ? request.getLanguage() : "zh")
                .build();
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookResponse updateBook(String id, UpdateBookRequest request, Long userId) {
        Book book = bookRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getGenre() != null) book.setGenre(request.getGenre());
        if (request.getStatus() != null) book.setStatus(request.getStatus());
        if (request.getOutline() != null) book.setOutline(request.getOutline());
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(String id, Long userId) {
        Book book = bookRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        bookRepository.delete(book);
    }
}
