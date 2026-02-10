package com.aiw.backend.app.model.notification.domain;

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
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "Notifications")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Notification {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, columnDefinition = "longtext")
  private String message;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false, columnDefinition = "tinyint", length = 1)
  private Boolean activated;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdatedAt;

  //마이페이지 알림 설정 필드 추가
  @Column
  private Boolean meetingAlarm = true; // 기본값 설정

  @Column
  private Boolean deadlineAlarm = true;

  @Column
  private Boolean allAlarm = true;

}
