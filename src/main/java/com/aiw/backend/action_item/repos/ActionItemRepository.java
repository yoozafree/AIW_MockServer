package com.aiw.backend.action_item.repos;

import com.aiw.backend.action_item.domain.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    ActionItem findFirstByMeetingId(Long id);

    boolean existsByAssigneeMemberIdIgnoreCase(String assigneeMemberId);

}
