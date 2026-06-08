package com.inkos.dto.response;
import com.inkos.entity.Chapter;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class ChapterResponse {
    private final Long id;
    private final String bookId;
    private final int chapterNumber;
    private final String title;
    private final String content;
    private final int wordCount;
    private final String status;
    private final int version;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ChapterResponse from(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId()).bookId(chapter.getBookId())
                .chapterNumber(chapter.getChapterNumber()).title(chapter.getTitle())
                .content(chapter.getContent())
                .wordCount(chapter.getWordCount() != null ? chapter.getWordCount() : 0)
                .status(chapter.getStatus())
                .version(chapter.getVersion() != null ? chapter.getVersion() : 1)
                .createdAt(chapter.getCreatedAt()).updatedAt(chapter.getUpdatedAt()).build();
    }
}
