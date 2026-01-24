package com.aiw.backend.team_member.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TeamMemberDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String role;

    @NotNull
    private Long member;

    @NotNull
    private Long team;

}
