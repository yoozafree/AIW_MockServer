package com.aiw.backend.app.model.personal_memo.service;

import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.app.model.personal_memo.domain.PersonalMemo;
import com.aiw.backend.app.model.personal_memo.dto.PersonalMemoDTO;
import com.aiw.backend.app.model.personal_memo.repository.PersonalMemoRepository;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class PersonalMemoService {

    private final PersonalMemoRepository personalMemoRepository;
    private final MemberRepository memberRepository;

    public PersonalMemoService(final PersonalMemoRepository personalMemoRepository, final MemberRepository memberRepository) {
        this.personalMemoRepository = personalMemoRepository;
        this.memberRepository = memberRepository;
    }

    // 조회 로직
    @Transactional(readOnly = true)
    public PersonalMemoDTO getMemo(final Long memberId) {
        PersonalMemo memo = personalMemoRepository.findFirstByMemberId(memberId);
        if (memo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메모가 존재하지 않습니다.");
        }
        return mapToDTO(memo, false);
    }

    // 작성 및 수정 로직 (Post)
    public PersonalMemoDTO saveOrUpdate(final Long memberId, final String content) {
        PersonalMemo memo = personalMemoRepository.findFirstByMemberId(memberId);
        boolean isNew = false;

        if (memo == null) {
            // 없으면 생성
            isNew = true;
            memo = new PersonalMemo();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
            memo.setMember(member);
            memo.setActivated(true);
        }

        memo.setContent(content);
        PersonalMemo saved = personalMemoRepository.save(memo);

        return mapToDTO(saved, isNew);
    }

    private PersonalMemoDTO mapToDTO(PersonalMemo memo, boolean isNew) {
        return PersonalMemoDTO.builder()
                .id(memo.getId())
                .content(memo.getContent())
                .created(isNew)
                .createdAt(memo.getDateCreated())
                .updatedAt(memo.getLastUpdated())
                .build();
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final PersonalMemo memberPersonalMemo = personalMemoRepository.findFirstByMemberId(event.getId());
        if (memberPersonalMemo != null) {
            referencedException.setKey("member.personalMemo.member.referenced");
            referencedException.addParam(memberPersonalMemo.getId());
            throw referencedException;
        }
    }

}
