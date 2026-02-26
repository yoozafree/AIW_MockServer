package com.aiw.backend.app.controller.api.mainpage;

import com.aiw.backend.app.model.announcement.dto.AnnouncementDTO;
import com.aiw.backend.app.model.announcement.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/announcements", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(final AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    public ResponseEntity<AnnouncementDTO> createAnnouncement(
            @RequestBody @Valid final AnnouncementDTO announcementDTO,
            @RequestParam(name = "memberId") final Long currentMemberId) {

        // DTO에 작성자 정보를 담아 서비스로 전달
        AnnouncementDTO created = announcementService.create(announcementDTO, currentMemberId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
