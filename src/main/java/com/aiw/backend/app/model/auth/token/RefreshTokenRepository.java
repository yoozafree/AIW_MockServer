package com.aiw.backend.app.model.auth.token;


import com.aiw.backend.app.model.auth.token.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByAtId(String atId);
    void deleteByAtId(String atId);
}
