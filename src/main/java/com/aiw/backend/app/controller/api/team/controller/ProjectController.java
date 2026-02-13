package com.aiw.backend.app.controller.api.team.controller;

import com.aiw.backend.app.model.project.dto.ProjectDTO;
import com.aiw.backend.app.model.project.service.ProjectService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody @Valid final ProjectDTO projectDTO) {
        final ProjectDTO createdProject = projectService.create(projectDTO);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    //조회
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(projectService.get(id));
    }

    //수정
    @PostMapping("/update")
    public ResponseEntity<ProjectDTO> updateProject(
            @RequestParam(name = "id") final Long id, // 쿼리 파라미터
            @RequestBody @Valid final ProjectDTO projectDTO) {

        final ProjectDTO updatedProject = projectService.update(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    //삭제
    @PostMapping("/delete")
    public ResponseEntity<ProjectDTO> deleteProject(
            @RequestParam(name = "id") final Long id,
            @RequestHeader(value = "Authorization") String authHeader) {

        final Long currentMemberId = 1L;

        final ProjectDTO deletedProject = projectService.delete(id, currentMemberId);

        return ResponseEntity.ok(deletedProject);
    }
}
