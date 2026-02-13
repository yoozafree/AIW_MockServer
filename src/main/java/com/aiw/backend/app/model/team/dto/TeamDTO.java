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

    //탈퇴 성공 여부
    private Boolean left;

    //요청시 권한 위임할 대상 ID
    private Long delegateMemberId;

    //응답 시 새 팀장 ID
    private Long newLeaderId;

}
