package com.aiw.backend.app.controller.api.mainpage.controller;

import com.aiw.backend.app.model.daily_brief.dto.DailyBriefDTO;
import com.aiw.backend.app.model.daily_brief.service.DailyBriefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/dashboard/daily-brief", produces = MediaType.APPLICATION_JSON_VALUE)
public class DailyBriefController {
    private final DailyBriefService dailyBriefService;

    @GetMapping
    public ResponseEntity<DailyBriefDTO> getDailyBrief(
            @RequestParam(name = "memberId") final Long memberId) {

        // 서비스에서 요약, AI 코멘트, 회의/투두 리스트가 합쳐진 DTO를 가져옴
        DailyBriefDTO dailyBrief = dailyBriefService.getDailyBrief(memberId);

        return ResponseEntity.ok(dailyBrief);
    }
}
