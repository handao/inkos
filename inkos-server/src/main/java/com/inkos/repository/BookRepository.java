package com.inkos.repository;

import com.inkos.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {
    Page<Book> findByUserId(Long userId, Pageable pageable);
    List<Book> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<Book> findByIdAndUserId(String id, Long userId);
}
