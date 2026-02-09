package com.aiw.backend.app.model.team.service;

import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.app.model.team_member.domain.TeamMember;
import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.dto.TeamDTO;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.util.CustomCollectors;
import com.aiw.backend.util.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository; // 추가
    private final com.aiw.backend.app.model.team_member.repository.TeamMemberRepository teamMemberRepository;
    private final ApplicationEventPublisher publisher;

    public TeamService(final TeamRepository teamRepository,
                       final MemberRepository memberRepository,
                       final com.aiw.backend.app.model.team_member.repository.TeamMemberRepository teamMemberRepository,
            final ApplicationEventPublisher publisher) {
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.publisher = publisher;
    }

    public List<TeamDTO> findAll() {
        final List<Team> teams = teamRepository.findAll(Sort.by("id"));
        return teams.stream()
                .map(team -> mapToDTO(team, new TeamDTO()))
                .toList();
    }

    public TeamDTO get(final Long id) {
        return teamRepository.findById(id)
                .map(team -> mapToDTO(team, new TeamDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TeamDTO teamDTO) {
        // 1. 팀 엔티티 생성 및 기본 매핑 (이름 등)
        final Team team = new Team();
        team.setName(teamDTO.getName());
        team.setInviteCode(UUID.randomUUID().toString().substring(0, 8));
        team.setActivated(true);

        final Team savedTeam = teamRepository.save(team);

        // 3. 생성자(현재 로그인 유저)를 TeamMember로 등록
        // 현재는 인증 로직 전이므로 테스트용으로 1L 사용 (추후 Security 적용 시 변경)
        final Long currentMemberId = 1L;
        final Member creator = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        final TeamMember teamMember = new TeamMember();
        teamMember.setMember(creator);
        teamMember.setTeam(savedTeam);
        teamMember.setRole("LEADER"); // 팀장 역할 부여
        teamMember.setActivated(true);

        teamMemberRepository.save(teamMember);

        return savedTeam.getId();
    }

    public boolean update(final Long id, final TeamDTO teamDTO) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // 명세서 상 이름 위주 수정
        if (teamDTO.getName() != null) {
            team.setName(teamDTO.getName());
            teamRepository.save(team);
            return true;
        }
        return false;
    }

    public boolean delete(final Long id) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        // 1. 활성화된 팀원 수 체크
        long activeCount = teamMemberRepository.countByTeamIdAndActivatedTrue(id);

        if (activeCount > 1) {
            // 팀원이 더 남아있으면 삭제 실패
            return false;
        }

        // 2. 마지막 1인이면 Soft Delete 진행
        team.setActivated(false);
        teamRepository.save(team);

        publisher.publishEvent(new BeforeDeleteTeam(id));

        return true;
    }

    private TeamDTO mapToDTO(final Team team, final TeamDTO teamDTO) {
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setInviteCode(team.getInviteCode());
        teamDTO.setActivated(team.getActivated());
        return teamDTO;
    }

    private Team mapToEntity(final TeamDTO teamDTO, final Team team) {
        team.setName(teamDTO.getName());
        team.setInviteCode(teamDTO.getInviteCode());
        team.setActivated(teamDTO.getActivated());
        return team;
    }

    public Map<Long, Long> getTeamValues() {
        return teamRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Team::getId, Team::getId));
    }

}
