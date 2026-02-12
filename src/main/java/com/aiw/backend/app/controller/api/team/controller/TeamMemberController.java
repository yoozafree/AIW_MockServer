package com.aiw.backend.app.controller.api.team.controller;

import com.aiw.backend.app.model.team_member.dto.TeamMemberDTO;
import com.aiw.backend.app.model.team_member.service.TeamMemberService;
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
@RequestMapping(value = "/api/teamMembers", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(final TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    public ResponseEntity<List<TeamMemberDTO>> getAllTeamMembers() {
        return ResponseEntity.ok(teamMemberService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberDTO> getTeamMember(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(teamMemberService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTeamMember(
            @RequestBody @Valid final TeamMemberDTO teamMemberDTO) {
        final Long createdId = teamMemberService.create(teamMemberDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTeamMember(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TeamMemberDTO teamMemberDTO) {
        teamMemberService.update(id, teamMemberDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable(name = "id") final Long id) {
        teamMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
