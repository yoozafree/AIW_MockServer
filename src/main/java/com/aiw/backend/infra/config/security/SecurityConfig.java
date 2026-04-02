package com.aiw.backend.infra.config.security;

import com.aiw.backend.infra.auth.jwt.JwtAuthenticationEntryPoint;
import com.aiw.backend.infra.auth.jwt.filter.JwtAuthenticationFilter;
import com.aiw.backend.infra.auth.jwt.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .logout(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            (requests) -> requests
                .requestMatchers("/favicon.ico", "/img/**", "/js/**","/css/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                .requestMatchers("/", "/error", "/auth/login", "/auth/signup").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/members/**").permitAll()
                    .requestMatchers("/api/teams/**").permitAll()
                    .requestMatchers("/api/teamMembers/**").permitAll()
                    .requestMatchers("/api/projects/**").permitAll()
                    .requestMatchers("/api/meetings/**").permitAll()
                    .requestMatchers("/api/actionItems/**").permitAll()
                    .requestMatchers("/api/v1/mypage/**").permitAll()
                    .requestMatchers("/api/v1/comments/**").permitAll()
                    .requestMatchers("/api/personalMemos/**").permitAll()
                    .requestMatchers("/api/announcements/**").permitAll()
                    .requestMatchers("/api/notifications/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        )
        // jwtAuthenticationEntryPoint 는 oauth 인증을 사용할 경우 제거
        .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}

