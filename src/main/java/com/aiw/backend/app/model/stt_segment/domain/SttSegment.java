package com.aiw.backend.app.model.stt_segment.domain;

import com.aiw.backend.app.model.meeting_speaker_map.domain.MeetingSpeakerMap;
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
@Table(name = "SttSegments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class SttSegment {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String speakerLabel;

  @Column(nullable = false)
  private String startMs;

  @Column(nullable = false)
  private String endMs;

  @Column(nullable = false, unique = true, columnDefinition = "longtext")
  private String segText;

  @Column(nullable = false, columnDefinition = "tinyint", length = 1)
  private Boolean activated;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meeting_speaker_map_id", nullable = false)
  private MeetingSpeakerMap meetingSpeakerMap;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime dateCreated;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdated;

}
