package com.inkos.dto.response;
import com.inkos.entity.Book;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class BookDetailResponse {
    private final String id;
    private final String title;
    private final String genre;
    private final String status;
    private final String language;
    private final String fanficMode;
    private final int chaptersWritten;
    private final String outline;
    private final String coverImageUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<ChapterResponse> chapters;

    public static BookDetailResponse from(Book book, List<ChapterResponse> chapters) {
        return BookDetailResponse.builder()
                .id(book.getId()).title(book.getTitle())
                .genre(book.getGenre()).status(book.getStatus())
                .language(book.getLanguage()).fanficMode(book.getFanficMode())
                .chaptersWritten(book.getChaptersWritten() != null ? book.getChaptersWritten() : 0)
                .outline(book.getOutline()).coverImageUrl(book.getCoverImageUrl())
                .createdAt(book.getCreatedAt()).updatedAt(book.getUpdatedAt())
                .chapters(chapters).build();
    }
}
