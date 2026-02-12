package com.aiw.backend.app.controller.api.team.controller;

import com.aiw.backend.app.model.project.dto.ProjectDTO;
import com.aiw.backend.app.model.project.service.ProjectService;
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
}
