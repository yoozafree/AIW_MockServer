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

    //팀장 ID
    private Long leaderId;
    //팀장 이름
    private String leaderName;
    //수정 삭제 성공 메시지 응답용
    private String message;

}
