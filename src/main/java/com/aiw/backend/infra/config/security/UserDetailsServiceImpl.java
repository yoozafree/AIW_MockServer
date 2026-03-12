package com.aiw.backend.infra.config.security;

import com.aiw.backend.app.model.auth.domain.Principal;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {
    // username: 로그인 식별자 -> email 사용
    Member member = memberRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));

    List<SimpleGrantedAuthority> authorities
        = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    return Principal.createPrincipal(member, authorities);
  }

//  private List<SimpleGrantedAuthority> findUserAuthorities(Member member) {
//    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
//  }
}