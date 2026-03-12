package com.aiw.backend.app.model.member.repository;

import com.aiw.backend.app.model.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNameIgnoreCase(String name);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);


}
