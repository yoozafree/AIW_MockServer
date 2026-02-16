package com.aiw.backend.app.model.daily_brief.repository;


import com.aiw.backend.app.model.daily_brief.domain.DailyBrief;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DailyBriefRepository extends JpaRepository<DailyBrief, Long> {

    DailyBrief findFirstByMemberId(Long id);

}
