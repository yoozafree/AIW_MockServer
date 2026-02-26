package com.aiw.backend.app.controller.api.mainpage;

import com.aiw.backend.app.model.personal_memo.dto.PersonalMemoDTO;
import com.aiw.backend.app.model.personal_memo.service.PersonalMemoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/personalMemos", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonalMemoController {
    private final PersonalMemoService personalMemoService;

    public PersonalMemoController(PersonalMemoService personalMemoService) {
        this.personalMemoService = personalMemoService;
    }

    // 조회
    @GetMapping
    @Operation(summary = "개인 메모 조회", description = "사용자의 개인 메모를 조회합니다.")
    public ResponseEntity<PersonalMemoDTO> getMemo(
            @RequestParam(name = "memberId") final Long memberId) {

        return ResponseEntity.ok(personalMemoService.getMemo(memberId));
    }

    // 작성 및 수정
    @PostMapping
    @Operation(summary = "개인 메모 저장/수정", description = "사용자의 개인 메모 내용을 저장하거나 업데이트합니다.")
    public ResponseEntity<PersonalMemoDTO> saveMemo(
            @RequestParam(name = "memberId") final Long memberId,
            @RequestBody @Valid final PersonalMemoDTO requestDTO) {

        PersonalMemoDTO response = personalMemoService.saveOrUpdate(memberId, requestDTO.getContent());
        return ResponseEntity.ok(response);
    }
}
