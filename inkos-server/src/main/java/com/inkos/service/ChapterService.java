package com.inkos.service;

import com.inkos.dto.request.CreateChapterRequest;
import com.inkos.dto.response.ChapterResponse;
import com.inkos.entity.Chapter;
import com.inkos.exception.BusinessException;
import com.inkos.exception.ErrorCode;
import com.inkos.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;

    public List<ChapterResponse> listChapters(String bookId) {
        return chapterRepository.findByBookIdOrderByChapterNumberAsc(bookId)
                .stream().map(ChapterResponse::from).toList();
    }

    public ChapterResponse getChapter(Long id, Long userId) {
        Chapter chapter = chapterRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return ChapterResponse.from(chapter);
    }

    @Transactional
    public ChapterResponse createChapter(String bookId, CreateChapterRequest request, Long userId) {
        Chapter chapter = Chapter.builder()
                .bookId(bookId)
                .userId(userId)
                .chapterNumber(request.getChapterNumber())
                .title(request.getTitle())
                .content(request.getContent())
                .wordCount(request.getContent() != null ? request.getContent().length() : 0)
                .build();
        return ChapterResponse.from(chapterRepository.save(chapter));
    }

    @Transactional
    public ChapterResponse updateChapter(Long id, CreateChapterRequest request, Long userId) {
        Chapter chapter = chapterRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (request.getTitle() != null) chapter.setTitle(request.getTitle());
        if (request.getContent() != null) {
            chapter.setContent(request.getContent());
            chapter.setWordCount(request.getContent().length());
        }
        return ChapterResponse.from(chapterRepository.save(chapter));
    }

    @Transactional
    public void deleteChapter(Long id, Long userId) {
        Chapter chapter = chapterRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        chapterRepository.delete(chapter);
    }
}
