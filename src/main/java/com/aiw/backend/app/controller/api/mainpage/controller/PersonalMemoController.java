package com.aiw.backend.app.controller.api.mainpage.controller;

import com.aiw.backend.app.model.personal_memo.dto.PersonalMemoDTO;
import com.aiw.backend.app.model.personal_memo.service.PersonalMemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal-memos")
public class PersonalMemoController {
    private final PersonalMemoService personalMemoService;

    public PersonalMemoController(PersonalMemoService personalMemoService) {
        this.personalMemoService = personalMemoService;
    }

    // 조회
    @GetMapping
    public ResponseEntity<PersonalMemoDTO> getMemo(@RequestHeader("Authorization") String authHeader) {
        // 인증 로직 연동 전까지 하드코딩된 ID 1L 사용
        Long currentMemberId = 1L;
        return ResponseEntity.ok(personalMemoService.getMemo(currentMemberId));
    }

    // 작성 및 수정
    @PostMapping
    public ResponseEntity<PersonalMemoDTO> saveMemo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PersonalMemoDTO requestDTO) {

        Long currentMemberId = 1L;
        PersonalMemoDTO response = personalMemoService.saveOrUpdate(currentMemberId, requestDTO.getContent());

        return ResponseEntity.ok(response);
    }
}
