package com.aiw.backend.member.repos;

import com.aiw.backend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNameIgnoreCase(String name);

}
