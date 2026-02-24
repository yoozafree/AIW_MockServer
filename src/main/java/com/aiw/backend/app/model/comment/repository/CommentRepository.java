package com.aiw.backend.app.model.comment.repository;

import com.aiw.backend.app.model.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findFirstByMemberId(Long id);
    Optional<Comment> findByMemberIdAndRefTypeAndRefId(Long memberId, String refType, Long refId);

}
