package com.inkos.repository;

import com.inkos.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByBookIdOrderByChapterNumberAsc(String bookId);
    Optional<Chapter> findByIdAndUserId(Long id, Long userId);
    int countByBookId(String bookId);
}
