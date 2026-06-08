package com.inkos.repository;

import com.inkos.entity.TruthFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TruthFileRepository extends JpaRepository<TruthFile, Long> {
    List<TruthFile> findByBookId(String bookId);
    Optional<TruthFile> findByBookIdAndFilePath(String bookId, String filePath);
}
