package com.aiw.backend.app.model.project.service;

import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.project.domain.Project;
import com.aiw.backend.app.model.project.repository.ProjectRepository;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Project teamProject = projectRepository.findFirstByTeamId(event.getId());
        if (teamProject != null) {
            referencedException.setKey("team.project.team.referenced");
            referencedException.addParam(teamProject.getProjectId());
            throw referencedException;
        }
    }

}
