package com.aiw.backend.app.model.team.service;

import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.dto.TeamDTO;
import com.aiw.backend.app.model.team.repos.TeamRepository;
import com.aiw.backend.util.CustomCollectors;
import com.aiw.backend.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final ApplicationEventPublisher publisher;

    public TeamService(final TeamRepository teamRepository,
            final ApplicationEventPublisher publisher) {
        this.teamRepository = teamRepository;
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
        final Team team = new Team();
        mapToEntity(teamDTO, team);
        return teamRepository.save(team).getId();
    }

    public void update(final Long id, final TeamDTO teamDTO) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(teamDTO, team);
        teamRepository.save(team);
    }

    public void delete(final Long id) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteTeam(id));
        teamRepository.delete(team);
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
