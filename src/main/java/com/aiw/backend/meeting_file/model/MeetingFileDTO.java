package com.aiw.backend.meeting_file.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeetingFileDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String fileType;

    @NotNull
    @Size(max = 255)
    @MeetingFileOriginalFilenameUnique
    private String originalFilename;

    @NotNull
    @Size(max = 255)
    @MeetingFileStorageUrlUnique
    private String storageUrl;

    private Long durationSec;

    @NotNull
    private LocalDateTime uploadedAt;

    @NotNull
    private LocalDateTime recordedAt;

    @NotNull
    @MeetingFileMeetingUnique
    private Long meeting;

}
