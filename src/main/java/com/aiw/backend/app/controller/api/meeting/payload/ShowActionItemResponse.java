package com.aiw.backend.app.controller.api.meeting.payload;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowActionItemResponse {
    Long actionItemId;
    LocalDateTime dueDate;
    String content;
    String assignee;
    String status;

}
