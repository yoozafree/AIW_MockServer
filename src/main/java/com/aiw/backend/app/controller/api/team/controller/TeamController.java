package com.aiw.backend.app.controller.api.team.controller;

import com.aiw.backend.app.model.team.dto.TeamDTO;
import com.aiw.backend.app.model.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/teams", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeamController {

    private final TeamService teamService;

    public TeamController(final TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(teamService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTeam(@RequestBody @Valid final TeamDTO teamDTO) {
        return new ResponseEntity<>(teamService.create(teamDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateTeam(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TeamDTO teamDTO) {
        // Service에서 boolean을 반환하도록 수정했음
        return ResponseEntity.ok(teamService.update(id, teamDTO));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Boolean> deleteTeam(@PathVariable(name = "id") final Long id) {
        // Soft Delete 로직 적용 및 결과 반환
        return ResponseEntity.ok(teamService.delete(id));
    }

    //초대 링크 가입
    @PostMapping("/join")
    @Operation(summary = "초대 링크 가입", description = "초대 코드를 통해 팀에 가입합니다.")
    public ResponseEntity<TeamDTO> joinTeam(
            @RequestParam(name = "code") String inviteCode,
            @RequestParam(name = "memberId") Long memberId) { // ID를 직접 입력받음

        return ResponseEntity.ok(teamService.joinTeam(inviteCode, memberId));
    }

    @PutMapping("/{id}/leave")
    @Operation(summary = "팀 탈퇴 및 위임", description = "팀원이 탈퇴하며, 팀장일 경우 권한을 위임합니다.")
    public ResponseEntity<TeamDTO> leaveTeam(
            @PathVariable(name = "id") final Long id,
            @RequestParam(name = "memberId") final Long memberId,
            @RequestBody TeamDTO teamDTO) {
        // 테스트용: 현재 로그인 유저가 ID: 2이라고 가정
        return ResponseEntity.ok(teamService.leaveTeam(id, memberId, teamDTO.getDelegateMemberId()));
    }

}
