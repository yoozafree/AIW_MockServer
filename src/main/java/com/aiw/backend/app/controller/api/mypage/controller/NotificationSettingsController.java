package com.aiw.backend.app.controller.api.mypage.controller;

import com.aiw.backend.app.model.notification.dto.NotificationDTO;
import com.aiw.backend.app.model.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/mypage/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor

public class NotificationSettingsController {

    private final NotificationService notificationService;

    //마이페이지: 알림 설정 조회
    @GetMapping("/settings")
    @Operation(summary = "알림 설정 조회", description = "마이페이지에서 사용자의 회의, 마감, 전체 알림 설정을 조회합니다.")
    public ResponseEntity<NotificationDTO> getSettings() {
        // 실제 운영 시에는 토큰에서 정보를 가져오지만, 현재는 테스트용 1L 고정
        final Long currentMemberId = 1L;
        return ResponseEntity.ok(notificationService.getSettings(currentMemberId));
    }

    //마이페이지: 알림 설정 수정
    @PostMapping("/settings")
    @Operation(summary = "알림 설정 수정", description = "사용자가 원하는 알림 항목(회의, 마감, 전체)을 켜거나 끕니다.")
    public ResponseEntity<NotificationDTO> updateSettings(@RequestBody final NotificationDTO notificationDTO) {
        final Long currentMemberId = 1L;
        final Boolean isUpdated = notificationService.updateSettings(currentMemberId, notificationDTO);

        // 명세서에 맞춰 updated 필드에 결과 담아서 반환
        return ResponseEntity.ok(NotificationDTO.builder()
                .updated(isUpdated)
                .build());
    }
}
