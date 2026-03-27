package com.aiw.backend.app.model.action_item.repository;

import com.aiw.backend.app.model.action_item.domain.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

  ActionItem findFirstByMeeting_Id(Long id);

  ActionItem findFirstByAssigneeMemberId(Long id);

  // AI 데일리 피드백용: 오늘 마감인 투두 목록 조회, 담당자별
  List<ActionItem> findByAssigneeMemberIdAndDueDateBetween(Long memberId, LocalDateTime start, LocalDateTime end);

  List<ActionItem> findByMeetingProjectId(Long projectId);
}
