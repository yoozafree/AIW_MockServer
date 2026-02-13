package com.aiw.backend.app.model.action_item.repository;

import com.aiw.backend.app.model.action_item.domain.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

  ActionItem findFirstByMeetingId(Long id);

  ActionItem findFirstByAssigneeMemberId(Long id);

}
