package com.aiw.backend.app.model.stt_segment.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SttSegmentDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String speakerLabel;

    @NotNull
    @Size(max = 255)
    private String startMs;

    @NotNull
    @Size(max = 255)
    private String endMs;

    @NotNull
    @SttSegmentSegTextUnique
    private String segText;

    @NotNull
    private Long meetingSpeakerMap;

}
