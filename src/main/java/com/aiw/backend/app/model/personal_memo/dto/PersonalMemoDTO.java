package com.aiw.backend.app.model.personal_memo.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalMemoDTO {

    private Long id;
    private String content;
    private Boolean created; // true: 생성, false: 수정 응답용
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}