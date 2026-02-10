package com.aiw.backend.app.model.team_member.service;

import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.app.model.team_member.domain.TeamMember;
import com.aiw.backend.app.model.team_member.dto.TeamMemberDTO;
import com.aiw.backend.app.model.team_member.repository.TeamMemberRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public TeamMemberService(final TeamMemberRepository teamMemberRepository,
            final MemberRepository memberRepository, final TeamRepository teamRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    public List<TeamMemberDTO> findAll() {
        final List<TeamMember> teamMembers = teamMemberRepository.findAll(Sort.by("id"));
        return teamMembers.stream()
                .map(teamMember -> mapToDTO(teamMember, new TeamMemberDTO()))
                .toList();
    }

    public TeamMemberDTO get(final Long id) {
        return teamMemberRepository.findById(id)
                .map(teamMember -> mapToDTO(teamMember, new TeamMemberDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TeamMemberDTO teamMemberDTO) {
        final TeamMember teamMember = new TeamMember();
        mapToEntity(teamMemberDTO, teamMember);
        return teamMemberRepository.save(teamMember).getId();
    }

    public void update(final Long id, final TeamMemberDTO teamMemberDTO) {
        final TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(teamMemberDTO, teamMember);
        teamMemberRepository.save(teamMember);
    }

    public void delete(final Long id) {
        final TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        teamMemberRepository.delete(teamMember);
    }

    private TeamMemberDTO mapToDTO(final TeamMember teamMember, final TeamMemberDTO teamMemberDTO) {
        teamMemberDTO.setId(teamMember.getId());
        teamMemberDTO.setRole(teamMember.getRole());
        teamMemberDTO.setMember(teamMember.getMember() == null ? null : teamMember.getMember().getId());
        teamMemberDTO.setTeam(teamMember.getTeam() == null ? null : teamMember.getTeam().getId());
        return teamMemberDTO;
    }

    private TeamMember mapToEntity(final TeamMemberDTO teamMemberDTO, final TeamMember teamMember) {
        teamMember.setRole(teamMemberDTO.getRole());
        final Member member = teamMemberDTO.getMember() == null ? null : memberRepository.findById(teamMemberDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        teamMember.setMember(member);
        final Team team = teamMemberDTO.getTeam() == null ? null : teamRepository.findById(teamMemberDTO.getTeam())
                .orElseThrow(() -> new NotFoundException("team not found"));
        teamMember.setTeam(team);
        return teamMember;
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final TeamMember memberTeamMember = teamMemberRepository.findFirstByMemberId(event.getId());
        if (memberTeamMember != null) {
            referencedException.setKey("member.teamMember.member.referenced");
            referencedException.addParam(memberTeamMember.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final TeamMember teamTeamMember = teamMemberRepository.findFirstByTeamId(event.getId());
        if (teamTeamMember != null) {
            referencedException.setKey("team.teamMember.team.referenced");
            referencedException.addParam(teamTeamMember.getId());
            throw referencedException;
        }
    }

}
