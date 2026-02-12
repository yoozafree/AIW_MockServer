package com.aiw.backend.app.model.team_member.repository;

import com.aiw.backend.app.model.team_member.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    TeamMember findFirstByMemberId(Long id);

    TeamMember findFirstByTeamId(Long id);

    //팀 삭제 시 해당 팀에 멤버 몇 명인지 확인
    long countByTeamIdAndActivatedTrue(Long teamId);
    //팀 탈퇴 시 특정 팀에 속한 특정 멤버의 관계 엔티티 찾기
    Optional<TeamMember> findByTeamIdAndRoleAndActivatedTrue(Long teamId, String role);
    //특정 팀에 특정 멤버가 이미 있는지 확인할 때 사용
    Optional<TeamMember> findByTeamIdAndMemberId(Long teamId, Long memberId);
}
