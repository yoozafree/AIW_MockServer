package com.aiw.backend.app.model.stt_state.domain;

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
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "SttStates")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class SttState {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String state;

  @Column(nullable = false)
  private String language;

  @Column(nullable = false, unique = true)
  private String rawJsonUrl;

  @Column(nullable = false)
  private String startedAt;

  @Column(nullable = false)
  private String endedAt;

  @Column(nullable = false, columnDefinition = "longtext")
  private String errorMessage;

  @Column(columnDefinition = "tinyint", length = 1)
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
