package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.Users;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);

    Optional<Users> findByEmailIgnoreCase(String email);

    boolean existsByMobileNumberAndIdNotAndDeletedAtIsNull(@NotBlank String mobileNumber, Long id);

//    Optional<Object> findByUsernameAndDeletedAtIsNull(String username);
}
