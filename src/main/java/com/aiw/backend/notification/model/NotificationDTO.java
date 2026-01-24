package com.aiw.backend.notification.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotificationDTO {

    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Long member;

}
