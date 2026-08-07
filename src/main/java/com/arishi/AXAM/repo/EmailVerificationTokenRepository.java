package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {


    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);


    void deleteByUserId(Long userId);

}
