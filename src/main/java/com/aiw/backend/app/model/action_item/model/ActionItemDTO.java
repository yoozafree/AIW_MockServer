package com.aiw.backend.app.model.action_item.model;

import com.aiw.backend.app.model.file.model.FileData;
import com.aiw.backend.app.model.file.model.ValidFileType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionItemDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 255)
    @ActionItemAssigneeMemberIdUnique
    private String assigneeMemberId;

    @NotNull
    private LocalDateTime dueDate;

    @NotNull
    private Boolean completed;

    @NotNull
    @Size(max = 255)
    private String memo;

    @NotNull
    @Valid
    @ValidFileType({"jpeg", "jpg", "png"})
    private FileData image;

    @NotNull
    private Integer phase;

    @NotNull
    @Size(max = 255)
    private String scope;

    @NotNull
    private Long meeting;

}
