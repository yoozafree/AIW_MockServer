package com.aiw.backend.app.model.announcement.service;


import com.aiw.backend.app.model.announcement.domain.Announcement;
import com.aiw.backend.app.model.announcement.repository.AnnouncementRepository;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(final AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Announcement teamAnnouncement = announcementRepository.findFirstByTeamId(event.getId());
        if (teamAnnouncement != null) {
            referencedException.setKey("team.announcement.team.referenced");
            referencedException.addParam(teamAnnouncement.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final Announcement writerAnnouncement = announcementRepository.findFirstByWriterId(event.getId());
        if (writerAnnouncement != null) {
            referencedException.setKey("member.announcement.writer.referenced");
            referencedException.addParam(writerAnnouncement.getId());
            throw referencedException;
        }
    }

}