package com.aiw.backend.app.model.personal_memo.service;

import com.aiw.backend.app.model.personal_memo.domain.PersonalMemo;
import com.aiw.backend.app.model.personal_memo.repository.PersonalMemoRepository;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


@Service
public class PersonalMemoService {

    private final PersonalMemoRepository personalMemoRepository;

    public PersonalMemoService(final PersonalMemoRepository personalMemoRepository) {
        this.personalMemoRepository = personalMemoRepository;
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
