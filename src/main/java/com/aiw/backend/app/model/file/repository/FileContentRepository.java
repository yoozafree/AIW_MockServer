package com.aiw.backend.app.model.file.repos;

import com.aiw.backend.app.model.file.domain.FileContent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileContentRepository extends JpaRepository<FileContent, String> {
}
