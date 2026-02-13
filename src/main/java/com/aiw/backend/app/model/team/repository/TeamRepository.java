package com.aiw.backend.app.model.team.repository;

import com.aiw.backend.app.model.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<Team, Long> {

}
