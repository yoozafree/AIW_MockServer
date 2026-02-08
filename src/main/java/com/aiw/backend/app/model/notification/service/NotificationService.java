package com.aiw.backend.app.model.notification.service;

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

    public NotificationService(final NotificationRepository notificationRepository,
            final MemberRepository memberRepository) {
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
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
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setMember(notification.getMember() == null ? null : notification.getMember().getId());

        //마이페이지: 알림 설정 필드 매핑 추가
        notificationDTO.setMeetingAlarm(notification.getMeetingAlarm());
        notificationDTO.setDeadlineAlarm(notification.getDeadlineAlarm());
        notificationDTO.setAllAlarm(notification.getAllAlarm());

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
        notification.setMessage(notificationDTO.getMessage());
        final Member member = notificationDTO.getMember() == null ? null : memberRepository.findById(notificationDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        notification.setMember(member);
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
