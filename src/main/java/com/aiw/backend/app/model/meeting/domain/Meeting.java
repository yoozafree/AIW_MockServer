package com.aiw.backend.app.model.meeting.domain;

import com.aiw.backend.app.model.project.domain.Project;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "Meetings")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Meeting {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String agenda;

  @Column(nullable = false)
  private LocalDateTime scheduledAt;

  @Column(nullable = false)
  private LocalDateTime startedAt;

  @Column(nullable = false)
  private LocalDateTime endedAt;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private String createdType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(nullable = false, columnDefinition = "tinyint", length = 1)
  private Boolean activated;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdatedAt;

}
