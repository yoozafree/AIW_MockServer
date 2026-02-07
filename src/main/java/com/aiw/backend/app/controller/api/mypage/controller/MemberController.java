package com.aiw.backend.app.controller.api.mypage.controller;

import com.aiw.backend.app.model.member.dto.MemberDTO;
import com.aiw.backend.app.model.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(memberService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createMember(@RequestBody @Valid final MemberDTO memberDTO) {
        final Long createdId = memberService.create(memberDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateMember(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final MemberDTO memberDTO) {
        memberService.update(id, memberDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteMember(@PathVariable(name = "id") final Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //마이페이지: 내 정보 조회
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "마이페이지에서 현재 로그인한 사용자의 정보를 조회합니다.")
    public ResponseEntity<MemberDTO> getMyInfo() {
        // 실제 운영 환경에서는 SecurityContext에서 ID를 추출하지만, 현재는 테스트용으로 1L 고정
        final Long currentMemberId = 1L;
        final MemberDTO memberDTO = memberService.getShowInfo(currentMemberId);
        return ResponseEntity.ok(memberDTO);
    }

    //마이페이지: 내 정보 수정
    @PostMapping("/me")
    @Operation(summary = "내 정보 수정", description = "마이페이지에서 사용자의 이름 및 관심 분야를 수정합니다.")
    public ResponseEntity<MemberDTO> updateMyInfo(@RequestBody @Valid final MemberDTO memberDTO) {
        final Long currentMemberId = 1L;
        memberService.updateMyInfo(currentMemberId, memberDTO);

        // 수정 성공 메시지를 DTO의 message 필드에 담아 반환
        return ResponseEntity.ok(MemberDTO.builder()
                .message("Success")
                .build());
    }

}
