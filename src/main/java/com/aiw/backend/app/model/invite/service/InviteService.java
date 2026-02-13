package com.aiw.backend.app.model.invite.service;

import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.invite.domain.Invite;
import com.aiw.backend.app.model.invite.dto.InviteDTO;
import com.aiw.backend.app.model.invite.repository.InviteRepository;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class InviteService {

  private final InviteRepository inviteRepository;
  private final TeamRepository teamRepository;

  public InviteService(final InviteRepository inviteRepository,
      final TeamRepository teamRepository) {
    this.inviteRepository = inviteRepository;
    this.teamRepository = teamRepository;
  }

  public List<InviteDTO> findAll() {
    final List<Invite> invites = inviteRepository.findAll(Sort.by("id"));
    return invites.stream()
        .map(invite -> mapToDTO(invite, new InviteDTO()))
        .toList();
  }

  public InviteDTO get(final Long id) {
    return inviteRepository.findById(id)
        .map(invite -> mapToDTO(invite, new InviteDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public Long create(final InviteDTO inviteDTO) {
    final Invite invite = new Invite();
    mapToEntity(inviteDTO, invite);
    return inviteRepository.save(invite).getId();
  }

  public void update(final Long id, final InviteDTO inviteDTO) {
    final Invite invite = inviteRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    mapToEntity(inviteDTO, invite);
    inviteRepository.save(invite);
  }

  public void delete(final Long id) {
    final Invite invite = inviteRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    inviteRepository.delete(invite);
  }

  private InviteDTO mapToDTO(final Invite invite, final InviteDTO inviteDTO) {
    inviteDTO.setId(invite.getId());
    inviteDTO.setInviteToken(invite.getInviteToken());
    inviteDTO.setExpiresAt(invite.getExpiresAt());
    inviteDTO.setRevokedAt(invite.getRevokedAt());
    inviteDTO.setActivated(invite.getActivated());
    inviteDTO.setTeam(invite.getTeam() == null ? null : invite.getTeam().getId());
    return inviteDTO;
  }

  private Invite mapToEntity(final InviteDTO inviteDTO, final Invite invite) {
    invite.setInviteToken(inviteDTO.getInviteToken());
    invite.setExpiresAt(inviteDTO.getExpiresAt());
    invite.setRevokedAt(inviteDTO.getRevokedAt());
    invite.setActivated(inviteDTO.getActivated());
    final Team team = inviteDTO.getTeam() == null ? null : teamRepository.findById(inviteDTO.getTeam())
        .orElseThrow(() -> new NotFoundException("team not found"));
    invite.setTeam(team);
    return invite;
  }

  @EventListener(BeforeDeleteTeam.class)
  public void on(final BeforeDeleteTeam event) {
    final ReferencedException referencedException = new ReferencedException();
    final Invite teamInvite = inviteRepository.findFirstByTeamId(event.getId());
    if (teamInvite != null) {
      referencedException.setKey("team.invite.team.referenced");
      referencedException.addParam(teamInvite.getId());
      throw referencedException;
    }
  }


}
