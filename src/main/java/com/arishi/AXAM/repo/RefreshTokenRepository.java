package com.arishi.AXAM.repo;


import com.arishi.AXAM.model.RefreshToken;
import com.arishi.AXAM.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {


    Optional<RefreshToken> findByTokenHash(String tokenHash);


    List<RefreshToken> findByUser(Users user);


    void deleteByUser(Users user);

}
