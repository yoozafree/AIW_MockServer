package com.aiw.backend.app.controller.api.meeting.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSttSegmentResponse {
    int sequence;
    String speaker;
    String content;
    String startedAt;
    String endedAt;

}
