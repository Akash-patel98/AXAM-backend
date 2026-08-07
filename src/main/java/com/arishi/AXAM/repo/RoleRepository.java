package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Roles, Long> {


    Optional<Roles> findByName(String name);

}
