package com.aiw.backend.app.model.team_member.repos;

import com.aiw.backend.app.model.team_member.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    TeamMember findFirstByMemberId(Long id);

    TeamMember findFirstByTeamId(Long id);

}
