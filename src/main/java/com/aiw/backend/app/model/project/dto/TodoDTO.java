package com.aiw.backend.app.model.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoDTO {
    private String task;
    private boolean completed;
}
