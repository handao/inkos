package com.inkos.dto.response;
import com.inkos.entity.Book;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class BookResponse {
    private final String id;
    private final String title;
    private final String genre;
    private final String status;
    private final String language;
    private final int chaptersWritten;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId()).title(book.getTitle())
                .genre(book.getGenre()).status(book.getStatus())
                .language(book.getLanguage())
                .chaptersWritten(book.getChaptersWritten() != null ? book.getChaptersWritten() : 0)
                .createdAt(book.getCreatedAt()).updatedAt(book.getUpdatedAt()).build();
    }
}
