package com.aiw.backend.app.model.notification.service;

import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.app.model.notification.domain.Notification;
import com.aiw.backend.app.model.notification.dto.NotificationDTO;
import com.aiw.backend.app.model.notification.repository.NotificationRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public NotificationService(final NotificationRepository notificationRepository,
            final MemberRepository memberRepository, final TeamRepository teamRepository) {
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    //마이페이지: 알림 설정 조회
    public NotificationDTO getSettings(final Long memberId) {
        // 해당 멤버의 알림 설정 엔티티를 찾습니다.
        // findFirstByMemberId 또는 findByMemberId (Repository에 정의 필요) 사용
        final Notification notification = notificationRepository.findFirstByMemberId(memberId);

        if (notification == null) {
            throw new NotFoundException("알림 설정을 찾을 수 없습니다.");
        }

        return mapToDTO(notification, new NotificationDTO());
    }
    //마이페이지 알림 설정 수정
    public Boolean updateSettings(final Long memberId, final NotificationDTO notificationDTO) {
        final Notification notification = notificationRepository.findFirstByMemberId(memberId);

        if (notification == null) {
            throw new NotFoundException("알림 설정을 찾을 수 없습니다.");
        }

        // 명세서 요구사항: 전달된(Null이 아닌) 필드만 업데이트
        mapSettingsToEntity(notificationDTO, notification);
        notificationRepository.save(notification);
        return true;
    }

    //대시보드: 알림 조회
    public List<NotificationDTO> findAllByMember(final Long memberId) {
        final List<Notification> notifications = notificationRepository
                .findByMemberIdAndTypeIsNotOrderByDateCreatedDesc(memberId, "SETTING");

        return notifications.stream()
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .toList();
    }


    //팀장 공지 알림
    public void createAnnouncementNotification(final Long teamId, final Long authorId,
                                               final String title, final String message, final Long relatedId) {
        final Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

        final Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("작성자를 찾을 수 없습니다."));

        // 팀의 모든 멤버에게 알림 생성
        team.getTeamMembers().forEach(teamMember -> {
            final Notification notification = new Notification();
            notification.setMember(teamMember.getMember());
            notification.setAuthor(author);
            notification.setTeam(team);
            notification.setType("ANNOUNCEMENT");
            notification.setTitle(title);
            notification.setContent(message);
            notification.setRelatedId(relatedId);

            notificationRepository.save(notification);
        });
    }

    //AI 피드백 알림 생성
    public void createFeedbackNotification(final Long memberId, final String title,
                                           final String message, final Long relatedId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));

        final Notification notification = new Notification();
        notification.setMember(member);
        notification.setType("FEEDBACK");
        notification.setTitle(title);
        notification.setContent(message);
        notification.setRelatedId(relatedId);

        notificationRepository.save(notification);
    }

    public List<NotificationDTO> findAll() {
        final List<Notification> notifications = notificationRepository.findAll(Sort.by("id"));
        return notifications.stream()
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .toList();
    }

    public NotificationDTO get(final Long id) {
        return notificationRepository.findById(id)
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final NotificationDTO notificationDTO) {
        final Notification notification = new Notification();
        mapToEntity(notificationDTO, notification);
        return notificationRepository.save(notification).getId();
    }

    public void update(final Long id, final NotificationDTO notificationDTO) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(notificationDTO, notification);
        notificationRepository.save(notification);
    }

    public void delete(final Long id) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        notificationRepository.delete(notification);
    }

    private NotificationDTO mapToDTO(final Notification notification,
            final NotificationDTO notificationDTO) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setContent(notification.getContent());
        notificationDTO.setMember(notification.getMember() == null ? null : notification.getMember().getId());

        //마이페이지: 알림 설정 필드 매핑 추가
        notificationDTO.setMeetingAlarm(notification.getMeetingAlarm());
        notificationDTO.setDeadlineAlarm(notification.getDeadlineAlarm());
        notificationDTO.setAllAlarm(notification.getAllAlarm());

        //대시보드: 알림 필드 매핑 추가
        notificationDTO.setType(notification.getType());
        notificationDTO.setTitle(notification.getTitle());
        notificationDTO.setTeamId(notification.getTeam() == null ? null : notification.getTeam().getId());
        notificationDTO.setRelatedId(notification.getRelatedId());
        notificationDTO.setAuthorId(notification.getAuthor() == null ? null : notification.getAuthor().getId());
        notificationDTO.setAuthorName(notification.getAuthor() == null ? null : notification.getAuthor().getName());
        notificationDTO.setDateCreated(notification.getCreatedAt());
        notificationDTO.setLastUpdated(notification.getLastUpdatedAt());

        return notificationDTO;
    }

    //마이페이지 전용 맵핑: nullable 필드 처리
    private void mapSettingsToEntity(final NotificationDTO dto, final Notification entity) {
        if (dto.getMeetingAlarm() != null) {
            entity.setMeetingAlarm(dto.getMeetingAlarm());
        }
        if (dto.getDeadlineAlarm() != null) {
            entity.setDeadlineAlarm(dto.getDeadlineAlarm());
        }
        if (dto.getAllAlarm() != null) {
            entity.setAllAlarm(dto.getAllAlarm());
        }
    }

    private Notification mapToEntity(final NotificationDTO notificationDTO,
            final Notification notification) {
        notification.setContent(notificationDTO.getContent());
        final Member member = notificationDTO.getMember() == null ? null : memberRepository.findById(notificationDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        notification.setMember(member);

        // 대시보드 필드 매핑
        if (notificationDTO.getType() != null) {
            notification.setType(notificationDTO.getType());
        }
        if (notificationDTO.getTitle() != null) {
            notification.setTitle(notificationDTO.getTitle());
        }
        if (notificationDTO.getTeamId() != null) {
            final Team team = teamRepository.findById(notificationDTO.getTeamId())
                    .orElseThrow(() -> new NotFoundException("team not found"));
            notification.setTeam(team);
        }
        if (notificationDTO.getRelatedId() != null) {
            notification.setRelatedId(notificationDTO.getRelatedId());
        }
        if (notificationDTO.getAuthorId() != null) {
            final Member author = memberRepository.findById(notificationDTO.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("author not found"));
            notification.setAuthor(author);
        }

        return notification;
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final Notification memberNotification = notificationRepository.findFirstByMemberId(event.getId());
        if (memberNotification != null) {
            referencedException.setKey("member.notification.member.referenced");
            referencedException.addParam(memberNotification.getId());
            throw referencedException;
        }
    }

}
