package com.aiw.backend.app.model.action_item.domain;

import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.member.domain.Member;
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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "ActionItems")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ActionItem {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDateTime dueDate;

  @Column(nullable = false, columnDefinition = "tinyint", length = 1)
  private Boolean completed;

  @Column(nullable = false)
  private String memo;

  @Column(nullable = false)
  private String image;

  @Column(nullable = false)
  private Long phase;

  @Column(nullable = false)
  private String scope;

  @Column(nullable = false, columnDefinition = "tinyint", length = 1)
  private Boolean activated;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meeting_id", nullable = false)
  private Meeting meeting;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_member_id")
  private Member assigneeMember;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime dateCreated;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdated;

}
