package com.aiw.backend.team.repos;

import com.aiw.backend.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<Team, Long> {
}
