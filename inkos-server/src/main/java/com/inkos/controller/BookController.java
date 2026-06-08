package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.dto.request.CreateBookRequest;
import com.inkos.dto.request.UpdateBookRequest;
import com.inkos.dto.response.BookDetailResponse;
import com.inkos.dto.response.BookResponse;
import com.inkos.security.UserContext;
import com.inkos.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ApiResponse<Page<BookResponse>> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.listBooks(user.getUserId(), page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getBook(
            @PathVariable String id,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.getBook(id, user.getUserId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.createBook(request, user.getUserId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<BookResponse> updateBook(
            @PathVariable String id,
            @Valid @RequestBody UpdateBookRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.updateBook(id, request, user.getUserId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteBook(
            @PathVariable String id,
            @AuthenticationPrincipal UserContext user) {
        bookService.deleteBook(id, user.getUserId());
        return ApiResponse.success();
    }
}
