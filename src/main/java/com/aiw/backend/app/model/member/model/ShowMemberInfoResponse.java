package com.aiw.backend.app.model.member.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowMemberInfoResponse {
    private String email;
    private String name;
    private String interestedField;
    private String provider;
}
