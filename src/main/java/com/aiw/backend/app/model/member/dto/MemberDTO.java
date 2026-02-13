package com.aiw.backend.app.model.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String provider;

    @NotNull
    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String interestedField;

    @NotNull
    private Boolean activated;

    //ModifyMemberInfo 완료 후 성공 메세지
    private String message;

}
