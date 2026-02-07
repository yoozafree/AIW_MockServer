package com.aiw.backend.app.model.member.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyMemberInfoRequest {
    private String name;
    private String interestedField;
}
