package com.arishi.AXAM.config;

import com.arishi.AXAM.model.Roles;
import com.arishi.AXAM.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        List.of("CANDIDATE", "ADMIN").forEach(roleName -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Roles role = new Roles();
                role.setName(roleName);
                roleRepository.save(role);
            }
        });
    }

}
