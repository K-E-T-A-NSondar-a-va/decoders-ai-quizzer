package com.decoder.aiquizzer.repository;

import com.decoder.aiquizzer.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUsername(String username);

    @Query("select max(id) from VerificationToken")
    public Long findLastVerificationTokenId();
}
