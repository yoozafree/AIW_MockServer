package com.aiw.backend.app.model.personal_memo.repository;


import com.aiw.backend.app.model.personal_memo.domain.PersonalMemo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonalMemoRepository extends JpaRepository<PersonalMemo, Long> {

    PersonalMemo findFirstByMemberId(Long id);

}
