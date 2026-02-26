package com.aiw.backend.app.controller.api.mypage;

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
    public ResponseEntity<NotificationDTO> getSettings(@RequestParam(name = "memberId") final Long memberId) {
        return ResponseEntity.ok(notificationService.getSettings(memberId));
    }

    //마이페이지: 알림 설정 수정
    @PostMapping("/settings")
    @Operation(summary = "알림 설정 수정", description = "사용자가 원하는 알림 항목(회의, 마감, 전체)을 켜거나 끕니다.")
    public ResponseEntity<NotificationDTO> updateSettings(
            @RequestParam(name = "memberId") final Long memberId,
            @RequestBody final NotificationDTO notificationDTO) {

        final Boolean isUpdated = notificationService.updateSettings(memberId, notificationDTO);

        // 수정 성공 여부와 함께 업데이트된 정보를 다시 조회해서 보낼 수도 있고, 명세대로 응답할 수 있습니다.
        return ResponseEntity.ok(NotificationDTO.builder()
                .updated(isUpdated)
                .build());
    }
}
