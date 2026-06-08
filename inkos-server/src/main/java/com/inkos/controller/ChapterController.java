package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.dto.request.CreateChapterRequest;
import com.inkos.dto.response.ChapterResponse;
import com.inkos.security.UserContext;
import com.inkos.service.BookService;
import com.inkos.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books/{bookId}/chapters")
@RequiredArgsConstructor
public class ChapterController {
    private final BookService bookService;
    private final ChapterService chapterService;

    @GetMapping
    public ApiResponse<List<ChapterResponse>> listChapters(
            @PathVariable String bookId,
            @AuthenticationPrincipal UserContext user) {
        bookService.getBook(bookId, user.getUserId());
        return ApiResponse.success(chapterService.listChapters(bookId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChapterResponse> createChapter(
            @PathVariable String bookId,
            @Valid @RequestBody CreateChapterRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(chapterService.createChapter(bookId, request, user.getUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<ChapterResponse> getChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(chapterService.getChapter(id, user.getUserId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<ChapterResponse> updateChapter(
            @PathVariable Long id,
            @Valid @RequestBody CreateChapterRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(chapterService.updateChapter(id, request, user.getUserId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal UserContext user) {
        chapterService.deleteChapter(id, user.getUserId());
        return ApiResponse.success();
    }
}
