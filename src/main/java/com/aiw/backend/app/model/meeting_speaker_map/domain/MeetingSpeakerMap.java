package com.aiw.backend.app.model.meeting_speaker_map.domain;

import com.aiw.backend.app.model.meeting.domain.Meeting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "MeetingSpeakerMaps")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MeetingSpeakerMap {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false, length = 20)
  private String speakerLabel;

  @Column(nullable = false, columnDefinition = "tinyint", length = 1)
  private Boolean activated;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meeting_id", nullable = false)
  private Meeting meeting;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdatedAt;

}
