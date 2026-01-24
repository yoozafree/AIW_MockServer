package com.aiw.backend.member.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String provider;

    @NotNull
    @Size(max = 255)
    @MemberEmailUnique
    private String email;

    @NotNull
    @Size(max = 255)
    @MemberNameUnique
    private String name;

    @Size(max = 255)
    private String interestedField;

    @NotNull
    private Boolean activated;

}
