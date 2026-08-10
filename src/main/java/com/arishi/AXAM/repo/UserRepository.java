package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);

}
