package com.aiw.backend.meeting_file.repos;

import com.aiw.backend.meeting_file.domain.MeetingFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingFileRepository extends JpaRepository<MeetingFile, Long> {

    MeetingFile findFirstByMeetingId(Long id);

    boolean existsByOriginalFilenameIgnoreCase(String originalFilename);

    boolean existsByStorageUrlIgnoreCase(String storageUrl);

    boolean existsByMeetingId(Long id);

}
