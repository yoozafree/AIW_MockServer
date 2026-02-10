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
import java.util.Optional;
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
        final Long currentMemberId = 2L;
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

    //팀 멤버 추가
    public TeamDTO joinTeam(final String inviteCode, final Long memberId){
        // 1. 초대 코드로 팀 찾기 (TeamRepository에 findByInviteCode 추가 필요)
        Team team = teamRepository.findAll().stream()
                .filter(t -> t.getInviteCode().equals(inviteCode) && t.getActivated())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("유효하지 않은 초대 코드입니다."));

        // 2. 이미 가입된 멤버인지 확인
        Optional<TeamMember> existingMember = teamMemberRepository.findByTeamIdAndMemberId(team.getId(), memberId);
        if (existingMember.isPresent() && existingMember.get().getActivated()) {
            throw new IllegalStateException("이미 가입된 팀입니다.");
        }

        // 3. 팀 멤버 등록 (또는 재활성화)
        TeamMember teamMember = existingMember.orElse(new TeamMember());
        teamMember.setTeam(team);
        teamMember.setMember(memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다.")));
        teamMember.setRole("MEMBER"); // 초대 링크로 들어오면 일반 멤버
        teamMember.setActivated(true);

        teamMemberRepository.save(teamMember);

        // 4. 응답 구성
        TeamDTO response = new TeamDTO();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setMessage("팀 참여에 성공했습니다.");
        return response;
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

    //팀 탈퇴
    public TeamDTO leaveTeam(final Long teamId, final Long memberId, final Long delegateMemberId) {
        // 1. 팀원 존재 여부 및 활성화 상태 확인
        TeamMember me = teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId)
                .orElseThrow(() -> new NotFoundException("팀 멤버 정보를 찾을 수 없습니다."));

        if (!me.getActivated()) {
            throw new IllegalStateException("이미 탈퇴 처리된 멤버입니다.");
        }

        TeamDTO response = new TeamDTO();
        response.setId(teamId);

        // 2. 팀장 권한 처리
        if ("LEADER".equals(me.getRole())) {
            if (delegateMemberId == null) {
                throw new IllegalStateException("팀장은 권한을 위임할 대상을 지정해야 탈퇴할 수 있습니다.");
            }

            // 위임받을 대상 찾기
            TeamMember successor = teamMemberRepository.findByTeamIdAndMemberId(teamId, delegateMemberId)
                    .orElseThrow(() -> new NotFoundException("권한을 위임받을 멤버를 찾을 수 없습니다."));

            if (!successor.getActivated()) {
                throw new IllegalStateException("탈퇴한 멤버에게는 권한을 위임할 수 없습니다.");
            }

            // 권한 위임 실행
            successor.setRole("LEADER");
            teamMemberRepository.save(successor);
            response.setNewLeaderId(successor.getMember().getId());
        }

        // 3. 본인 Soft Delete 처리 (activated = false)
        me.setActivated(false);
        teamMemberRepository.save(me);

        // 4. 응답 구성
        response.setLeft(true);
        response.setActivated(false);

        return response;
    }

    private TeamDTO mapToDTO(final Team team, final TeamDTO teamDTO) {
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setInviteCode(team.getInviteCode());
        teamDTO.setActivated(team.getActivated());

        //팀장 추가 로직
        teamMemberRepository.findByTeamIdAndRoleAndActivatedTrue(team.getId(), "LEADER")
                .ifPresent(leader -> {
                    teamDTO.setLeaderId(leader.getMember().getId());
                    teamDTO.setLeaderName(leader.getMember().getName());
                });

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
