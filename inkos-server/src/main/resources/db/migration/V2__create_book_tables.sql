CREATE TABLE book (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    genre VARCHAR(50),
    status VARCHAR(20) DEFAULT 'draft',
    language VARCHAR(10) DEFAULT 'zh',
    fanfic_mode VARCHAR(50),
    chapters_written INT DEFAULT 0,
    outline TEXT,
    cover_image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_book_user (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    chapter_number INT NOT NULL,
    title VARCHAR(200),
    content LONGTEXT,
    word_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'draft',
    version INT DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_chapter_book (book_id),
    INDEX idx_chapter_user (user_id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE truth_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    content LONGTEXT,
    content_type VARCHAR(50),
    version INT DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_truth_path (book_id, file_path),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
