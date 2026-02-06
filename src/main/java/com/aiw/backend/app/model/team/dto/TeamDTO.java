package com.aiw.backend.app.model.team.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TeamDTO {

    private Long id;

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String inviteCode;

    private Boolean activated;

}
