package com.aiw.backend.app.model.comment.service;


import com.aiw.backend.app.model.comment.domain.Comment;
import com.aiw.backend.app.model.comment.dto.CommentDTO;
import com.aiw.backend.app.model.comment.dto.FeedbackDTO;
import com.aiw.backend.app.model.comment.repository.CommentRepository;
import com.aiw.backend.app.model.meeting_summary.domain.MeetingSummary;
import com.aiw.backend.app.model.meeting_summary.repository.MeetingSummaryRepository;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MeetingSummaryRepository meetingSummaryRepository;

    public CommentService(final CommentRepository commentRepository, final MemberRepository memberRepository, final MeetingSummaryRepository meetingSummaryRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.meetingSummaryRepository = meetingSummaryRepository;
    }

    // 특정 타입의 코멘트 가져오기 (피드백 요약 등)
    public CommentDTO getCommentByRef(Long memberId, String refType, Long refId) {
        Comment comment = commentRepository.findByMemberIdAndRefTypeAndRefId(memberId, refType, refId)
                .orElseThrow(() -> new NotFoundException("해당 코멘트를 찾을 수 없습니다."));
        return mapToDTO(comment, new CommentDTO());
    }

    //피드백 요약 조회 (FEEDBACK_SUM)
    public FeedbackDTO getFeedbackSummary(Long memberId, Long meetingId) {
        // refType을 "FEEDBACK_SUM"으로 고정하여 조회합니다.
        Comment summary = commentRepository.findByMemberIdAndRefTypeAndRefId(memberId, "FEEDBACK_SUM", meetingId)
                .orElseThrow(() -> new NotFoundException("해당 회의에 대한 피드백 요약이 존재하지 않습니다."));

        // 엔티티를 DTO로 변환
        CommentDTO commentDTO = mapToDTO(summary, new CommentDTO());
        return FeedbackDTO.builder()
                .meetingId(meetingId)
                .feedbackSummary(mapToDTO(summary, new CommentDTO()))
                .build();
    }

    //상세 AI 피드백 조회
    public FeedbackDTO getFeedbackDetail(final Long memberId, final Long meetingId){
        Comment detail = commentRepository.findByMemberIdAndRefTypeAndRefId(memberId, "FEEDBACK", meetingId)
                .orElse(null);

        if (detail == null) {
            throw new NotFoundException("해당 회의에 대한 AI 피드백이 존재하지 않습니다.");
        }

        // FeedbackDTO 조립
        return FeedbackDTO.builder()
                .meetingId(meetingId)
                .feedbackDetail(mapToDTO(detail, new CommentDTO()))
                .build();

    }

    //피드백용 회의 요약 조회
    public FeedbackDTO getMeetingSummaryForFeedback(final Long meetingId) {
        //findFirstByMeetingId 메서드 사용
        MeetingSummary summary = meetingSummaryRepository.findFirstByMeetingId(meetingId);

        if (summary == null) {
            throw new NotFoundException("해당 회의의 요약 데이터가 아직 생성되지 않았습니다.");
        }

        return FeedbackDTO.builder()
                .meetingId(meetingId)
                .meetingSummary(summary.getSummaryText()) // 회의 내용 요약 텍스트만 추출
                .build();
    }

    private CommentDTO mapToDTO(final Comment comment, final CommentDTO dto) {
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setRefType(comment.getRefType());
        dto.setRefId(comment.getRefId());
        dto.setActivated(comment.getActivated());
        dto.setMemberId(comment.getMember().getId());
        dto.setDateCreated(comment.getDateCreated());
        dto.setLastUpdated(comment.getLastUpdated());
        return dto;
    }

    private Comment mapToEntity(final CommentDTO dto, final Comment comment) {
        comment.setContent(dto.getContent());
        comment.setRefType(dto.getRefType());
        comment.setRefId(dto.getRefId());
        comment.setActivated(dto.getActivated() != null ? dto.getActivated() : true);

        if (dto.getMemberId() != null) {
            Member member = memberRepository.findById(dto.getMemberId())
                    .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));
            comment.setMember(member);
        }
        return comment;
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final Comment memberComment = commentRepository.findFirstByMemberId(event.getId());
        if (memberComment != null) {
            referencedException.setKey("member.comment.member.referenced");
            referencedException.addParam(memberComment.getId());
            throw referencedException;
        }
    }

    //임시 테스트용도
    public void createMockData(Long memberId, Long meetingId) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        // 요약 데이터 생성
        Comment sum = new Comment();
        sum.setContent("테스트 요약 내용입니다.");
        sum.setRefType("FEEDBACK_SUM");
        sum.setRefId(meetingId);
        sum.setMember(member);
        sum.setActivated(true);
        commentRepository.save(sum);

        // 상세 데이터 생성
        Comment detail = new Comment();
        detail.setContent("테스트 상세 피드백 내용입니다.");
        detail.setRefType("FEEDBACK");
        detail.setRefId(meetingId);
        detail.setMember(member);
        detail.setActivated(true);
        commentRepository.save(detail);
    }

}
