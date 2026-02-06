package com.aiw.backend.app.model.invite.repository;

import com.aiw.backend.app.model.invite.domain.Invite;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InviteRepository extends JpaRepository<Invite, Long> {

    Invite findFirstByTeamId(Long id);

}
