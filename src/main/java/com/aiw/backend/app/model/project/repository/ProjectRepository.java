package com.aiw.backend.app.model.project.repository;

import com.aiw.backend.app.model.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findFirstByTeamId(Long id);

}
