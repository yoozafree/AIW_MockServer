package com.aiw.backend.app.model.announcement.repository;


import com.aiw.backend.app.model.announcement.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Announcement findFirstByTeamId(Long id);

    Announcement findFirstByWriterId(Long id);

}
