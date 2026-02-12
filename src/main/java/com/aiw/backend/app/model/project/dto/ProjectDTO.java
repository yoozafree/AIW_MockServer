package com.aiw.backend.app.model.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProjectDTO {
    private Long id;
    private String name;
    private LocalDateTime targetDate;
    private Long teamId; // 생성을 위해 teamId 필요

    private int progress; // 0~100 사이의 값
    private List<TodoDTO> todos; // 단계별 투두 리스트
}

