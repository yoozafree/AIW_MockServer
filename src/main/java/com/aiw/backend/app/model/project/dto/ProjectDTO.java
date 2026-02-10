package com.aiw.backend.app.model.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectDTO {
    private Long id;
    private String name;
    private LocalDateTime targetDate;
    private Long teamId; // 생성을 위해 teamId 필요
}
