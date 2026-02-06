package com.aiw.backend.app.model.meeting_file.domain;

import com.aiw.backend.app.model.meeting.domain.Meeting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "MeetingFiles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MeetingFile {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fileType;

  @Column(nullable = false, unique = true)
  private String originalFilename;

  @Column(nullable = false, unique = true)
  private String storageUrl;

  @Column(nullable = false)
  private Long durationSec;

  @Column(nullable = false)
  private LocalDateTime uploadedAt;

  @Column(nullable = false)
  private LocalDateTime recordedAt;

  @Column(nullable = false, columnDefinition = "tinyint", length = 1)
  private Boolean activated;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meeting_id", nullable = false, unique = true)
  private Meeting meeting;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime dateCreated;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdated;

}
