package com.aiw.backend.app.model.project.service;

import com.aiw.backend.app.model.project.dto.ProjectDTO;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.project.domain.Project;
import com.aiw.backend.app.model.project.repository.ProjectRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    //팀 확인용 추가
    private final TeamRepository teamRepository;

    public ProjectService(final ProjectRepository projectRepository, final TeamRepository teamRepository) {

        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Long create(final ProjectDTO projectDTO) {
        final Project project = new Project();

        // 1. 팀 존재 여부 확인
        Team team = teamRepository.findById(projectDTO.getTeamId())
                .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

        // 2. 엔티티 매핑
        project.setName(projectDTO.getName());
        project.setTargetDate(projectDTO.getTargetDate());
        project.setTeam(team);
        project.setActivated(true);

        return projectRepository.save(project).getId();
    }

    //조회 로직
    @Transactional(readOnly = true)
    public ProjectDTO get(final Long id) {
        return projectRepository.findById(id)
                .map(project -> mapToDTO(project, new ProjectDTO()))
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
    }

    private ProjectDTO mapToDTO(final Project project, final ProjectDTO projectDTO) {
        projectDTO.setId(project.getId()); // id 반영
        projectDTO.setName(project.getName()); // name 반영
        projectDTO.setTargetDate(project.getTargetDate());
        projectDTO.setTeamId(project.getTeam().getId());
        return projectDTO;
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Project teamProject = projectRepository.findFirstByTeamId(event.getId());
        if (teamProject != null) {
            referencedException.setKey("team.project.team.referenced");
            referencedException.addParam(teamProject.getId().toString());
            throw referencedException;
        }
    }

}
