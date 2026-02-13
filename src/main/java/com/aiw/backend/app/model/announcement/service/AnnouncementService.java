package com.aiw.backend.app.model.announcement.service;


import com.aiw.backend.app.model.announcement.domain.Announcement;
import com.aiw.backend.app.model.announcement.dto.AnnouncementDTO;
import com.aiw.backend.app.model.announcement.repository.AnnouncementRepository;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.app.model.notification.service.NotificationService;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.app.model.team_member.domain.TeamMember;
import com.aiw.backend.app.model.team_member.repository.TeamMemberRepository;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final NotificationService notificationService;

    public AnnouncementService(final AnnouncementRepository announcementRepository,
                               final MemberRepository memberRepository,
                               final TeamRepository teamRepository,
                               final TeamMemberRepository teamMemberRepository,
                               final NotificationService notificationService) {
        this.announcementRepository = announcementRepository;
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public AnnouncementDTO create(final AnnouncementDTO dto, final Long currentMemberId) {
        // 1. 팀 및 작성자 존재 확인
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다."));
        Member writer = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 2. 권한 확인 (팀장인지 체크)
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(team.getId(), currentMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "팀원 정보가 없습니다."));

        if (!"LEADER".equals(teamMember.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀장만 공지를 작성할 수 있습니다.");
        }

        // 3. 엔티티 생성 및 저장
        Announcement announcement = new Announcement();
        announcement.setContent(dto.getContent());
        announcement.setTeam(team);
        announcement.setWriter(writer);
        announcement.setActivated(true);

        Announcement saved = announcementRepository.save(announcement);

        // 4. 팀원들에게 알림 전송 (연동!)
        notificationService.createAnnouncementNotification(
                team.getId(),
                writer.getId(),
                "새로운 팀 공지사항", // 알림 제목
                announcement.getContent(), // 알림 내용
                saved.getId() // 관련 엔티티 ID (공지사항 ID)
        );

        return mapToDTO(saved, new AnnouncementDTO());
    }

    private AnnouncementDTO mapToDTO(final Announcement announcement, final AnnouncementDTO dto) {
        dto.setId(announcement.getId());
        dto.setContent(announcement.getContent());
        dto.setActivated(announcement.getActivated());
        dto.setTeamId(announcement.getTeam().getId());
        dto.setWriterId(announcement.getWriter().getId());
        dto.setWriterName(announcement.getWriter().getName());
        dto.setDateCreated(announcement.getDateCreated());
        dto.setLastUpdated(announcement.getLastUpdated());
        return dto;
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