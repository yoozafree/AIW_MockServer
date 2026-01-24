package com.aiw.backend.file.repos;

import com.aiw.backend.file.domain.FileContent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileContentRepository extends JpaRepository<FileContent, String> {
}
