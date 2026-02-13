package com.aiw.backend.app.controller.api.mainpage.controller;

import com.aiw.backend.app.model.announcement.dto.AnnouncementDTO;
import com.aiw.backend.app.model.announcement.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(final AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    public ResponseEntity<AnnouncementDTO> createAnnouncement(
            @RequestBody @Valid final AnnouncementDTO announcementDTO,
            @RequestHeader("Authorization") String authHeader) {

        // 현재는 하드코딩된 ID 1L 사용 (나중에 payload/Security 연동)
        final Long currentMemberId = 1L;

        AnnouncementDTO created = announcementService.create(announcementDTO, currentMemberId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
