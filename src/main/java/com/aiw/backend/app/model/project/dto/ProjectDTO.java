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

    //삭제
    private Boolean activated; // 프로젝트 활성 상태 (false면 삭제된 프로젝트)
    private String message;    // 성공/실패 메시지 전달용
    private java.time.OffsetDateTime deletedAt; // 삭제 시점 응답용
}

