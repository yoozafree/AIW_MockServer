package com.aiw.backend.app.model.team.domain;

import com.aiw.backend.app.model.member.domain.Member;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "Teams")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Team {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  private String inviteCode;

  @Column(columnDefinition = "tinyint", length = 1)
  private Boolean activated;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdatedAt;

  //팀장 정보
  //팀장 권한 넘겨주기 기능
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "leader_id")
  private Member leader;

  //팀 설명
  @Column(columnDefinition = "text")
  private String description;

}
